(function(window, undefined) {
    var salamaSocket = new SalamaSocket();

    var _requestIdSeq = 0;
    var DEFAULT_TIMEOUT_MS = 60000;

    function newRequestId() {
        return String(++ _requestIdSeq);
    }

    function SalamaSocket() {

        /**
         *
         * @param options {
               url: Optional. String,
               enableLog: Optional. Boolean,
           }
         */
        this.build = function(options) {
            return new CloudDataSocket(options);
        };

        this.Socket = Socket;
        this.buildUrlByUri = buildUrlByUri;
    }

    /**
     *
     * @param options {
           url: Optional. String,
           enableLog: Optional. Boolean,
       }
     * @constructor
     */
    function CloudDataSocket(options) {
        //arguments ----------------
        var _url = (options && options.url) ? options.url : buildUrlByUri("/cloudDataService.socket");
        var _enableLog = (options && options.enableLog) ? options.enableLog : false;

        //variables ----------------
        var _this = this;
        var _socket;

        //init socket
        initSocket();

        //exposed functions ----------------
        this.close = function() {
            _socket.close();
        };

        /**
         *
         * @param args
            {
              timeout: Optional. Integer,
              data: {
                  serviceType: Required. String,
                  serviceMethod: Required. String,
              },
              success: Optional. function(result) {},
              error: Optional. function() {},
            }
         */
        this.sendRequest = function (args) {
            var requestId = newRequestId();
            args.data.__requestId = requestId;

            var timeout = args.timeout ? args.timeout : DEFAULT_TIMEOUT_MS;
            var callbackName = buildCallbackName(requestId);
            var abortTimeout = "";

            window[callbackName] = function(result) {
                clearTimeout(abortTimeout);
                delete window[callbackName];

                if(args.success) {
                    args.success(result);
                }
            };

            abortTimeout = setTimeout(function() {
                delete window[callbackName];

                if(args.error) {
                    args.error("timeout");
                }
            }, timeout);

            //to json string
            var msg = JSON.stringify(args.data);
            _socket.sendMsg(msg);
        }

        //inner functions ----------------

        function initSocket() {
            _socket = new Socket({
                getUrl : function() {
                    return _url;
                },
                enableLog: _enableLog,
                onMsgReceive: onMsgReceive
            });
            _socket.connect();
        }

        function onMsgReceive(msg) {
            var respData = JSON.parse(msg);
            var requestId = respData.__requestId;

            if(requestId == null || requestId.length == 0) {
                return;
            }

            var callbackName = buildCallbackName(requestId);
            if(window[callbackName]) {
                window[callbackName].call(this, respData.result);
            }
        }

        function buildCallbackName(requestId) {
            return "__CloudDataSocket_callback_" + requestId;
        }
    }

    function buildUrlByUri(uri) {
        var wsURL = "";
        if(window.location.protocol == 'https:') {
            wsURL += "wss://";
        } else {
            wsURL += "ws://";
        }

        wsURL += window.location.host;

        var pathName = window.location.pathname;
        var indexOf2nd = pathName.indexOf('/', 1);
        if(indexOf2nd < 0) {
            wsURL += pathName;
        } else {
            wsURL += pathName.substring(0, indexOf2nd);
        }

        if(uri.charAt(0) != '/') {
            wsURL += "/";
        }
        wsURL += uri;

        return wsURL;
    }

    function Socket(args) {
        /////////// parameters ///////////////
        //required ---
        this.getUrl = args.getUrl;
        this.onConnected = args.onConnected;
        this.onDisconnected = args.onDisconnected;
        this.onMsgReceive = args.onMsgReceive;

        //optional ---
        this.enableLog = args.enableLog != undefined? args.enableLog : true;
        this.logger = args.logger;

        /////////////// socket ///////////////
        this.socket = null;


        /////////////// internal variables ////////////
        var _this = this;
        window.addEventListener('unload', function () {
            _this.close();
        });

        var _pendingSendMsgQueue = new Array();

        ////// WebSocket implementation ///////
        this.close = function() {
            if(this.socket) {
                this.socket.close();
            }
        };

        /**
         * @return true: already in connected state
         */
        this.connect = function() {
            if(this.socket != undefined
                && (this.socket.readyState == WebSocket.OPEN
                || this.socket.readyState == WebSocket.CONNECTING)
            ) {
                //connected
                return true;
            } else {
                var wsURL = this.getUrl();

                //close socket -------------------------
                if(this.socket != null) {
                    log("WebSocket close broken socket -----");

                    this.socket.close();
                    this.socket = null;
                } else {
                    //first connect
                    //wsURL += "&fetchOnBegin=true";
                }

                //init socket --------------------------
                log("WebSocket create and connect -----");
                if(window.WebSocket) {
                    this.socket = new WebSocket(wsURL);
                } else if (window.MozWebSocket) {
                    this.socket = new MozWebSocket(wsURL);
                } else {
                    alert("Your browser does not support web socket");
                    return;
                }
                log("connecting to url:" + wsURL);

                //this.socket.binaryType = "arraybuffer";

                this.socket.onopen = onopen;

                this.socket.onmessage = onmessage;

                this.socket.onclose = onclose;

                return false;
            }
        };

        this.isConnected = function() {
            return (_this.socket && _this.socket.readyState == WebSocket.OPEN);
        };

        this.sendMsg = function(msg) {
            send(msg);
        };

        //////////// private methods //////////////////
        function send(data) {
            try {
                //auto connect
                if(!_this.isConnected()) {
                    _pendingSendMsgQueue.push(data);
                    _this.connect();
                } else {
                    _this.socket.send(data);
                }
            } catch(err) {
                log(err);
            }
        };

        function onopen(e) {
            log("WebSocket connected -----");
            if(_pendingSendMsgQueue.length > 0) {
                try {
                    while(true) {
                        var msg = _pendingSendMsgQueue.shift();
                        if(msg == null) {
                            break;
                        }

                        _this.socket.send(msg);
                    }
                } catch (err) {
                    log(err);
                }
            }

            if(_this.onConnected) {
                _this.onConnected(e);
            }
        }

        function onclose(e) {
            log("WebSocket onclose()");

            if(_this.onDisconnected) {
                _this.onDisconnected(e);
            }
        }

        function onmessage(e) {
            if (e.data instanceof ArrayBuffer) {
                log("WebSocket onmessage() received Binary msg. byteLength:" + e.data.byteLength);
                return;
            } else {
                log("WebSocket onmessage() received String msg. length:" + e.data.length);
            }

            //parse msg
            _this.onMsgReceive(e.data);
        }

        function log(msg, obj) {
            if(_this.enableLog) {
                if(obj) {
                    if(typeof obj == "object") {
                        msg += objToString(obj);
                    } else {
                        msg += obj;
                    }
                }

                if(_this.logger) {
                    _this.logger(msg);
                } else {
                    console.log(msg);
                }
            }
        }

        function objToString(obj) {
            var str = "{";
            for(var name in obj) {
                str += " " + name + ":" + obj[name];
            }
            str += "}";

            return str;
        }
    }

    //Expose
    window.salamaSocket = salamaSocket;

})(window);
