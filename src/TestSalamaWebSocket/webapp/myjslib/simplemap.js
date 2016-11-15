//make Array.forEach for browsers such as IE8
( function() {
    if (!Array.prototype.forEach) {
        Array.prototype.forEach = function(callBack, thisArg) {
            if (!this) {
                throw new TypeError("this is undefined");
            }
            if (!callBack) {
                throw new TypeError("callBack is undefined");
            }

            var obj = Object(this);
            var len = obj.length >>> 0;
            //convert obj.length to UInt32

            if ( typeof callBack != "function") {
                throw new TypeError(callBack + " is not a function");
            }

            var T;
            if (thisArg) {
                T = thisArg;
            }

            var i = 0;
            while (i < len) {
                var e;
                if ( i in obj) {
                    e = obj[i];
                    callBack.call(T, e, i, obj);
                }

                i++;
            }
        };
    }
}());

SimpleMap = function() {
    var _indexMap = {};
    var _entryArray = [];
    var _size = 0;
    
    this.del = function(key) {
        var index = _indexMap[key];
        if(index != undefined) {
            delete _indexMap[key];
            delete _entryArray[index];
            
            _size --;
            
            return true;
        } else {
            return false;
        }
    };
    
    this.clear = function() {
        delete _indexMap;
        _indexMap = {};
        
        delete _entryArray;
        _entryArray = [];
        
        _size = 0;
    };
    
    this.size = function() {
        return _size;  
    };
    
    this.set = function(key, value) {
        if(key == undefined) {
            throw new TypeError("key is undefined");
        }
        
        var index = _indexMap[key];
        if(index == undefined) {
            _indexMap[key] = _entryArray.length;
            _entryArray.push({key: key, value: value});

            _size ++;
            
            return true;
        } else {
            _entryArray[index].value = value;
            return false;
        }
    };
    
    this.add = function(obj) {
        var cnt = 0;
        
        for(var key in obj) {
            if(this.set(key, obj[key])) {
                cnt ++;
            }
        }
        
        return cnt;
    };
    
    this.get = function(key) {
        var index = _indexMap[key];
        if(index == undefined) {
            return null;
        } else {
            return _entryArray[index].value;
        }
    };
    
    
    /**
     * @param sortby function(v1, v2), v1,v2 are type of {key:, value:}
     */
    this.sort = function(sortby) {
        if(sortby) {
            _entryArray.sort(sortby);
            rebuildIndexMap();
        } else {
            this.sortByKey();
        }
    };
    
    /**
     * @param isAsc true: sort in ascending order
     */
    this.sortByKey = function(isAsc) {
        var asc = true;
        if(isAsc != undefined) {
            asc = isAsc;
        }
        
        _entryArray.sort(function(v1, v2){
            if(v1.key == v2.key) {
                return 0;
            } else if((v1.key < v2.key) == asc) {
                return -1;
            } else {
                return 1;
            }
        });
        
        rebuildIndexMap();
    };
    
    /**
     * @param isAsc true: sort in ascending order
     */
    this.sortByValue = function(isAsc) {
        var asc = true;
        if(isAsc != undefined) {
            asc = isAsc;
        }
        
        _entryArray.sort(function(v1, v2){
            if(v1.value == v2.value) {
                return 0;
            } else if((v1.value < v2.value) == asc) {
                return -1;
            } else {
                return 1;
            }
        });
        
        rebuildIndexMap();
    };

    function rebuildIndexMap() {
        var entry;
        for(var i = 0; i < _entryArray.length; i++) {
            entry = _entryArray[i];
            _indexMap[entry.key] = i;
        }
    }
    
    /**
     * @param callBack function({key:, value:}, index, this of EG3.Map)
     */
    this.forEach = function(callBack, thisArg) {
        var T;
        if (thisArg) {
            T = thisArg;
        }
        
        for(var i = 0; i < _entryArray.length; i++) {
            if(_entryArray[i] != undefined) {
                callBack.call(T, _entryArray[i], i, this);    
            }
        }
    };
    
};

SimpleMap.prototype = {
    /**
     * output console log
     */
    log: function(msg) {
        if(msg) {
            console.log("EG3.Map " + msg + " -----");    
        } else {
            console.log("EG3.Map -----");
        }
        
        this.forEach(function(v){
            console.log(v);
        }); 
    },
};

