var ROOT_PATH='http://127.0.0.1:8080'


function formatDate(date) {
    var year = date.getFullYear();
    var month = ("0" + (date.getMonth() + 1)).slice(-2);
    var day = ("0" + date.getDate()).slice(-2);
    var hours = ("0" + date.getHours()).slice(-2);
    var minutes = ("0" + date.getMinutes()).slice(-2);
    var seconds = ("0" + date.getSeconds()).slice(-2);
    return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
}

function formatDate2(date) {
    var year = date.getFullYear();
    var month = ("0" + (date.getMonth() + 1)).slice(-2);
    var day = ("0" + date.getDate()).slice(-2);
    return year + "-" + month + "-" + day;
}

function objectToUrlParams(obj) {
    var params = [];
    for (var key in obj) {
        if (obj.hasOwnProperty(key)) {
            var value = obj[key];
            if (Array.isArray(value)) {
                // 如果值是数组，则按照 key[]=value1&key[]=value2 的格式处理
                for (var i = 0; i < value.length; i++) {
                    params.push(encodeURIComponent(key) + "[]=" + encodeURIComponent(value[i]));
                }
            } else {
                // 否则按照 key=value 的格式处理
                params.push(encodeURIComponent(key) + "=" + encodeURIComponent(value));
            }
        }
    }
    return params.join("&");
}