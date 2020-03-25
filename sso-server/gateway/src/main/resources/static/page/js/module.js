$(document).ready(
    function () {
        $('#reservation').daterangepicker(daterangconfig);

        var colModel = [
            {"data": "id"},
            {"data": "moduleName"},
            {"data": "remark"},
            {"data": "enable"}
        ];

        function callback(data) {
            $.each(data, function (index, item) {

                data[index]['id'] = ' <td>\n' +
                    ' <input type="checkbox" name="table_records" id="' + data[index]['id'] + '" class="flat">\n' +
                    '</td>';
                //data[index]['createDate'] =new Date(data[index]['createDate']).Format("yyyy/MM/dd HH:mm:ss");
            });
        }
        var buttons =   [{
            extend: 'selected',
            className: "btn btn-primary",
            text: '停用',
            action: function ( e, dt, button, config ) {

                var ids =[];
                dt.rows( { selected: true } ).nodes().to$().find("input").each(function(i,itme){
                    ids.push($(this).attr("id"));
                    return true;
                });

                $.ajax({
                    url: "/module/maintenance",//这个就是请求地址对应sAjaxSource
                    data: JSON.stringify({
                        id: ids.join(","),
                        valid:'INVALID',
                        mode: "MODIFY"
                    }),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
                    type: 'post',
                    dataType: 'json',
                    contentType: "application/json",
                    //async : false,
                    success: function (result) {   //后台执行成功的回调函数
                       if(result&&result.code=='1'){
                           alert("执行成功!");
                           datatable.draw();
                       }

                    }
                });
            }
        },{
            extend: 'selected',
            className: "btn btn-success",
            text: '启用',
            action: function ( e, dt, button, config ) {

                var ids =[];
                dt.rows( { selected: true } ).nodes().to$().find("input").each(function(i,itme){
                    ids.push($(this).attr("id"));
                    return true;
                });

                $.ajax({
                    url: "/module/maintenance",//这个就是请求地址对应sAjaxSource
                    data: JSON.stringify({
                        id: ids.join(","),
                        valid:'VALID',
                        mode: "MODIFY"
                    }),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
                    type: 'post',
                    dataType: 'json',
                    contentType: "application/json",
                    //async : false,
                    success: function (result) {   //后台执行成功的回调函数
                        if(result&&result.code=='1'){
                            alert("执行成功!");
                            datatable.draw();
                        }

                    }
                });
            }
        },{
            extend: 'selected',
            className: "btn btn-warning",
            text: '删除',
            action: function ( e, dt, button, config ) {


                var ids =[];
                dt.rows( { selected: true } ).nodes().to$().find("input").each(function(i,itme){
                    ids.push($(this).attr("id"));
                    return true;
                });

                $.ajax({
                    url: "/module/maintenance",//这个就是请求地址对应sAjaxSource
                    data: JSON.stringify({
                        id: ids.join(","),
                        mode: "DROP"
                    }),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
                    type: 'post',
                    dataType: 'json',
                    contentType: "application/json",
                    //async : false,
                    success: function (result) {   //后台执行成功的回调函数
                        if(result&&result.code=='1'){
                            alert("执行成功!");
                            datatable.draw();
                        }

                    }
                });
            }
        }];
        var datatable =dataTableInit('#datatable1', "/listModules", colModel, [
                {orderable: false, targets: [0]},{orderable: false, targets: [1]},{orderable: false, targets: [2]},{orderable: false, targets: [3]}
            ],buttons,'form', null, callback
        );

        $("form").submit(function (e) {
            e.preventDefault();


            datatable.draw();
        });
        // $.ajax({
        //         url: "localhost:8002/test",
        //         dataType: 'text',//服务器返回json格式数据
        //         type: 'get',//HTTP请求类型
        //         beforeSend: function (XMLHttpRequest) {
        //             var an = localStorage.getItem("Authorization");
        //             XMLHttpRequest.setRequestHeader("Authorization", an);
        //
        //         },
        //         success: function (data, textStatus, request) {
        //             alert(JSON.stringify(data));
        //             var Authorization = request.getResponseHeader("Authorization")
        //             if (Authorization&&localStorage.getItem("Authorization")&&Authorization!=localStorage.getItem("Authorization")) {
        //                 localStorage.setItem("Authorization", Authorization);
        //             }
        //         }
        //     }
        // );
    });
