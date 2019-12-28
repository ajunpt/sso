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
            {"data": "roleCode"},
            {"data": "roleName"},
            {"data": "moduleName"},
            {"data": "isRegister"},
            {"data": "permissionNames"}
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
                if(data[index]){
                    data[index]['isRegister'] =data[index]['isRegister']?'是':'否';
                }
                
                if(data[index]&&data[index]['permissions']){
                    var str ='';
                    var permissions = data[index]['permissions'];
                    $.each(permissions,function (index,item) {
                       str+=item.permissionName+",";
                    });
                    data[index]['permissionNames']= str.substring(0,str.length-1);
                    datas[index]['permissionNames']=data[index]['permissionNames'];

                }
            });
        }
        var buttons =   [{
            className: "btn-primary",
            text: '创建',
            action: function ( e, dt, button, config ) {
                $("#modifyForm [name=id]").val("");
                $("#modifyForm [name=mode]").val("ADD");
                $("#modifyForm [name=roleCode]").val("");
                $("#modifyForm [name=roleName]").val("");
                $("#modifyForm [name=moduleId]").val("");
                $("#modifyForm [name=permissionNames]").val("");
                $("#modifyForm [name=permissionIds]").val("");
                $("#modifyForm [name=isRegister]").val("");
                var zTree = $.fn.zTree.getZTreeObj("treeDemo");
                var nodes=zTree.transformToArray(zTree.getNodes());
                $.each(nodes,function (index,treeNode) {
                    zTree.checkNode(treeNode, false, null, false);
                    return true;
                })
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
                        $("#modifyForm [name=roleCode]").val(data.roleCode);
                        $("#modifyForm [name=roleName]").val(data.roleName);
                        $("#modifyForm [name=isRegister]").val(data.isRegister?'true':'false');
                        if(data.module){
                            $("#modifyForm [name=moduleId]").val(data.module.id);
                        }else{
                            $("#modifyForm [name=moduleId]").val("");
                        }
                        checkNode(data);
                        $('#modifyModal').modal('show');
                    }else{
                        $("#modifyForm [name=roleCode]").val("");
                        $("#modifyForm [name=roleName]").val("");
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
                    url: "/role/maintenance",//这个就是请求地址对应sAjaxSource
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
        var datatable =dataTableInit('#datatable1', "/role/list", colModel, [
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
                url: "/role/maintenance",//这个就是请求地址对应sAjaxSource
                data: JSON.stringify({
                    id: $("#modifyForm [name=id]").val(),
                    mode: $("#modifyForm [name=mode]").val(),
                    roleCode:$("#modifyForm [name=roleCode]").val(),
                    roleName:$("#modifyForm [name=roleName]").val(),
                    permissionIds:$("#modifyForm [name=permissionIds]").val(),
                    isRegister:$("#modifyForm [name=isRegister]").val(),
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
        function checkNode(data){
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            var nodes=zTree.transformToArray(zTree.getNodes());
            var permissions = data['permissions'];
            var permissionIds='';
            var str='';
            $.each(nodes,function (index,treeNode) {
                    zTree.checkNode(treeNode, false, null, false);
                    return true;
            })
            $.each(permissions,function (index,item) {
                str+=item.permissionName+",";
                permissionIds+=item.id+',';
                $.each(nodes,function (index,treeNode) {
                    if(treeNode.id==item.id){
                        zTree.checkNode(treeNode, !treeNode.checked, null, false);
                        return false;
                    }
                })

            });
            $("#modifyForm [name=permissionNames]").val("");
            $("#modifyForm [name=permissionIds]").val("");
            $("#modifyForm [name=permissionNames]").val(str.length>0?str.substring(0,str.length-1):"");
            $("#modifyForm [name=permissionIds]").val(permissionIds.length>0?permissionIds.substring(0,permissionIds.length-1):"");

        }
        var setting = {
            check: {
                enable: true,
                chkboxType: {"Y":"", "N":""}
            },
            view: {
                dblClickExpand: false
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            callback: {
                beforeClick: beforeClick,
                onCheck: onCheck
            }
        };


        $.ajax({
            url: "/authority/list",//这个就是请求地址对应sAjaxSource
            data: JSON.stringify({
                pageNo: 0,
                pageSize: 0
            }),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
            type: 'post',
            dataType: 'json',
            contentType: "application/json",
            async : false,
            success: function (result) {   //后台执行成功的回调函数
                if (result && result.list) {
                    var datas = result.list;
                    var zNodes = [];

                    for (var i = 0, length = datas.length; i < length; i++) {
                        var data = datas[i];
                        if (data) {
                            zNodes.push({
                                id: data.id,
                                pId: null,
                                name: data.permissionName,
                                open:true,
                                data: data
                            });
                        }
                    }

                    $.fn.zTree.init($("#treeDemo"), setting, zNodes);

                }
            }
        });

        function beforeClick(treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            zTree.checkNode(treeNode, !treeNode.checked, null, true);

            return false;
        }

        function onCheck(e, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
                nodes = zTree.getCheckedNodes(true),
                v = "";
            var permissionIds='';
            for (var i=0, l=nodes.length; i<l; i++) {
                v += nodes[i].name + ",";
                permissionIds+=nodes[i].id + ",";
            }
            if (v.length > 0 ) v = v.substring(0, v.length-1);
            if (permissionIds.length > 0 ) permissionIds = permissionIds.substring(0, permissionIds.length-1);
            var cityObj = $("#citySel");
            cityObj.val( v);
            $("#modifyForm [name=permissionIds]").val(permissionIds);
        }

        function showMenu() {
            var cityObj = $("#citySel");
            var cityOffset = $("#citySel").offset();
            $("#menuContent").css({left:cityOffset.left + "px", top:cityOffset.top + cityObj.outerHeight() + "px"}).slideDown("fast");

            $("body").bind("mousedown", onBodyDown);
        }
        function hideMenu() {
            $("#menuContent").fadeOut("fast");
            $("body").unbind("mousedown", onBodyDown);
        }
        function onBodyDown(event) {
            if (!(event.target.id == "menuBtn" || event.target.id == "citySel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
                hideMenu();
            }
        }

        $("#citySel").on('click',showMenu);
    });
