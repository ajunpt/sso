$(document).ready(
    function () {
        $('#reservation').daterangepicker(daterangconfig);
        var datas=[];
        function getDataById(id) {
            var data;
            $.each(datas, function (index, item) {
                if(item&&item.id==id){
                    data=item;
                    return false;
                }
            });
            return data;
        }
        var colModel = [
            {"data": "id"},
            {"data": "resourceName"},
            {"data": "uri"},
            {"data": "moduleName"}
        ];

        function callback(data) {
            if(data){
                $.each(data, function (index, item) {

                    datas[index]=JSON.parse(JSON.stringify(data[index]));
                });

            }
            $.each(data, function (index, item) {

                data[index]['id'] = ' <td>\n' +
                    ' <input type="checkbox" name="table_records" id="' + data[index]['id'] + '" class="flat">\n' +
                    '</td>';
                if(data[index]&&data[index]['module']){
                    data[index]['moduleName'] =data[index]['module']['moduleName']||'';
                }

            });
        }
        var buttons =   [{
            className: "btn-primary",
            text: '创建',
            action: function ( e, dt, button, config ) {
                $("#modifyForm [name=id]").val("");
                $("#modifyForm [name=mode]").val("ADD");
                $("#modifyForm [name=resourceName]").val("");
                $("#modifyForm [name=uri]").val("");
                $("#modifyForm [name=moduleId]").val("");
                $('#modifyModal').modal('show')
            }
        },{
            extend: 'selectedSingle',
            className: "btn-success",
            text: '修改',
            action: function ( e, dt, button, config ) {
                var ids =[];
                dt.rows( { selected: true } ).nodes().to$().find("input").each(function(i,itme){
                    ids.push($(this).attr("id"));
                    return true;
                });
                if(ids.length>1){
                    new MsgModal("提示","不能同时修改多条数据");
                    return;
                }else{
                    $("#modifyForm [name=id]").val(ids[0]);
                    $("#modifyForm [name=mode]").val("MODIFY");
                    var data = getDataById(ids[0]);
                    if(data){
                        $("#modifyForm [name=resourceName]").val(data.resourceName);
                        $("#modifyForm [name=uri]").val(data.uri);
                        if(data.module){
                            $("#modifyForm [name=moduleId]").val(data.module.id);
                        }else{
                            $("#modifyForm [name=moduleId]").val("");
                        }
                        $('#modifyModal').modal('show');
                    }else{
                        $("#modifyForm [name=resourceName]").val("");
                        $("#modifyForm [name=uri]").val("");
                        $("#modifyForm [name=moduleId]").val("");
                    }


                }

            }
        },{
            extend: 'selected',
            className: "btn-warning",
            text: '删除',
            action: function ( e, dt, button, config ) {
                var ids =[];
                dt.rows( { selected: true } ).nodes().to$().find("input").each(function(i,itme){
                    ids.push($(this).attr("id"));
                    return true;
                });
                $.ajax({
                    url: "/urlResource/maintenance",//这个就是请求地址对应sAjaxSource
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
                            new MsgModal("提示","执行成功!",function () {
                                datatable.draw();
                            })

                        }else{
                            new MsgModal("提示","执行失败!",function () {
                                datatable.draw();
                            })
                        }

                    }
                });
            }
        }];
        var datatable =dataTableInit('#datatable1', "/urlResource/list", colModel, [
                {orderable: false, targets: [0]},{orderable: false, targets: [1]},{orderable: false, targets: [2]},{orderable: false, targets: [3]}
            ],buttons,'#query', null, callback
        );

        $("#query").submit(function (e) {
            e.preventDefault();
            datatable.draw();
        });
        $("#modifyForm").submit(function (e) {
            e.preventDefault();
            $.ajax({
                url: "/urlResource/maintenance",//这个就是请求地址对应sAjaxSource
                data: JSON.stringify({
                    id: $("#modifyForm [name=id]").val(),
                    mode: $("#modifyForm [name=mode]").val(),
                    resourceName:$("#modifyForm [name=resourceName]").val(),
                    uri:$("#modifyForm [name=uri]").val(),
                    moduleId:$("#modifyForm [name=moduleId]").val()
                }),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                //async : false,
                success: function (result) {   //后台执行成功的回调函数
                   if(result&&result.code=='1'){
                       new MsgModal("提示","执行成功!",function () {
                           $('#modifyModal').modal('hide');
                           datatable.draw();
                       });

                   }else{
                       new MsgModal("提示","执行失败!",function () {
                           $('#modifyModal').modal('hide');
                       });
                   }

                }
            });

        });
    });
