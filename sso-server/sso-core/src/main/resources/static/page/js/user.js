$(document).ready(
    function () {
        $('#reservation').daterangepicker(daterangconfig);
        var datas = [];

        function getDataByUsername(username) {
            var data;
            $.each(datas, function (index, item) {
                if (item && item.username == username) {
                    data = item;
                    return false;
                }
            });
            return data;
        }

        var colModel = [
            {"data":"id"},
            {"data": "username"},
            {"data": "mobile"},
            {"data": "email"},
            {"data": "accountNonExpired"},
            {"data": "accountNonLocked"},
            {"data": "credentialsNonExpired"},
            {"data": "authorities"},
            {"data": "enable"}
        ];

        function callback(data) {
            if (data) {
                $.each(data, function (index, item) {
                    datas[index] = JSON.parse(JSON.stringify(data[index]));

                });

            }
            $.each(data, function (index, item) {
                data[index]['id'] = ' <td>\n' +
                    ' <input type="checkbox" name="table_records" id="' + data[index]['username'] + '" class="flat">\n' +
                    '</td>';
                var str = "";

                if (data[index] && data[index]['authorities']) {
                    var authorities = data[index]['authorities'];
                    $.each(authorities, function (i, item) {
                        str += item.authorityName + ",";
                    });

                    data[index]['authorities'] = str.length > 0 ? str.substring(0, str.length - 1) : "";
                }
                data[index]['accountNonExpired'] = data[index]['accountNonExpired'] ? '是' : '否';
                data[index]['accountNonLocked'] = data[index]['accountNonLocked'] ? '是' : '否';

                data[index]['credentialsNonExpired'] = data[index]['credentialsNonExpired'] ? '是' : '否';

                data[index]['enable'] = data[index]['enable'] ? '是' : '否';


            });
        }
        $("#modifyForm [name=authorities]").on('click', showMenu);
        function showMenu() {
            var cityObj = $("#modifyForm [name=authorities]");
            var cityOffset = $("#modifyForm [name=authorities]").offset();
            $("#menuContent").css({
                left: cityOffset.left + "px",
                top: cityOffset.top + cityObj.outerHeight() + "px"
            }).slideDown("fast");

            $("body").bind("mousedown", onBodyDown);
        }
        function onBodyDown(event) {
            if (!(event.target.id == "menuBtn"||event.target.id == "menuContent1" ||event.target.id == "authorities" || event.target.id == "superModuleName" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length > 0)) {
                hideMenu();
            }
        }
        function hideMenu() {
            $("#menuContent").fadeOut("fast");
            $("body").unbind("mousedown", onBodyDown);
        }
        var buttons = [
        //     {
        //     className: "btn-primary",
        //     text: '创建',
        //     action: function (e, dt, button, config) {
        //         $("#modifyForm [name=id]").val("");
        //         $("#modifyForm [name=mode]").val("ADD");
        //         $("#modifyForm [name=userName]").val("");
        //         $("#modifyForm [name=userAccount]").val("");
        //         $("#modifyForm [name=password]").val("");
        //         $("#modifyForm [name=mobile]").val("");
        //         $("#modifyForm [name=email]").val("");
        //         $("#modifyForm [name=permissionNames]").val("");
        //         $("#modifyForm [name=permissionIds]").val("");
        //         $("#modifyForm [name=roleNames]").val("");
        //         $("#modifyForm [name=roleIds]").val("");
        //         $("#modifyForm [name=authorityType]").val("INDIRECT");
        //
        //         var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        //         var nodes = zTree.transformToArray(zTree.getNodes());
        //         $.each(nodes, function (index, treeNode) {
        //             zTree.checkNode(treeNode, false, null, false);
        //             return true;
        //         })
        //
        //         var zTree1 = $.fn.zTree.getZTreeObj("treeDemo1");
        //         var nodes1 = zTree1.transformToArray(zTree1.getNodes());
        //         $.each(nodes1, function (index, treeNode) {
        //             zTree1.checkNode(treeNode, false, null, false);
        //             return true;
        //         })
        //
        //         $('#modifyModal').modal('show')
        //     }
        // },
            {
            extend: 'selectedSingle',
            className: "btn-success",
            text: '修改',
            action: function (e, dt, button, config) {
                var ids = [];
                dt.rows({selected: true}).nodes().to$().find("input").each(function (i, itme) {
                    ids.push($(this).attr("id"));
                    return true;
                });
                if (ids.length > 1) {
                    new MsgModal("提示", "不能同时修改多条数据");
                    return;
                } else {
                    $("#modifyForm [name=id]").val(ids[0]);
                    $("#modifyForm [name=mode]").val("MODIFY");
                    var data = getDataByUsername(ids[0]);
                    if (data) {
                        var str = "",str1="";

                        var authorities = data['authorities'];

                        $.each(authorities, function (i, item) {
                            str += item.authorityName + ",";
                            str1+=item.authority+",";
                        });
                        str = str.length>0?str.substring(0,str.length-1):"";
                        str1 = str1.length>0?str1.substring(0,str1.length-1):"";

                        $("#modifyForm [name=username]").val(data.username);
                        $("#modifyForm [name=mobile]").val(data.mobile);
                        $("#modifyForm [name=email]").val(data.email);
                        $("#modifyForm [name=accountNonExpired]").val(data.accountNonExpired+"");
                        $("#modifyForm [name=accountNonLocked]").val(data.accountNonLocked+"");
                        $("#modifyForm [name=credentialsNonExpired]").val(data.credentialsNonExpired+"");
                        $("#modifyForm [name=authorities]").val(str);
                        $("#modifyForm [name=authorities1]").val(str1);
                        $("#modifyForm [name=enable]").val(data.enable+"");
                        checkNode(authorities);
                        $('#modifyModal').modal('show');
                    }
                }
            }
        }
        // , {
        //     extend: 'selected',
        //     className: "btn-warning",
        //     text: '删除',
        //     action: function (e, dt, button, config) {
        //         var ids = [];
        //         dt.rows({selected: true}).nodes().to$().find("input").each(function (i, itme) {
        //             ids.push($(this).attr("id"));
        //             return true;
        //         });
        //         $.ajax({
        //             url: "/user/maintenance",//这个就是请求地址对应sAjaxSource
        //             data: JSON.stringify({
        //                 id: ids.join(","),
        //                 mode: "DROP"
        //             }),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
        //             type: 'post',
        //             dataType: 'json',
        //             contentType: "application/json",
        //             //async : false,
        //             success: function (result) {   //后台执行成功的回调函数
        //                 if (result && result.code == '1') {
        //                     new MsgModal("提示", "执行成功!", function () {
        //                         datatable.draw();
        //                     })
        //
        //                 } else {
        //                     new MsgModal("提示", "执行失败!", function () {
        //                         datatable.draw();
        //                     })
        //                 }
        //
        //             }
        //         });
        //     }
        // }
        ];
        var datatable = dataTableInit('#datatable1', "/user/listUsers", colModel, [
                // {orderable: false, targets: [0]}, {orderable: false, targets: [1]}, {
                //     orderable: false,
                //     targets: [2]
                // }, {orderable: false, targets: [3]}
            ], buttons, '#query', null, callback
        );

        $("#query").submit(function (e) {
            e.preventDefault();
            datatable.draw();
        });
        $("#modifyForm").submit(function (e) {
            e.preventDefault();
            var data = getDataByUsername($("#modifyForm [name=username]").val());
            data['mobile'] =$("#modifyForm [name=mobile]").val()?$("#modifyForm [name=mobile]").val():data['mobile'];
            data['email'] =$("#modifyForm [name=email]").val()?$("#modifyForm [name=email]").val():data['email'];
            data['accountNonExpired'] =$("#modifyForm [name=accountNonExpired]").val();
            data['accountNonLocked'] =$("#modifyForm [name=accountNonLocked]").val();
            data['credentialsNonExpired'] =$("#modifyForm [name=credentialsNonExpired]").val();
            data['enable'] =$("#modifyForm [name=enable]").val();
            var authorities1 = $("#modifyForm [name=authorities1]").val();
            var authorities=[];
            if(authorities1){
                authorities1 = authorities1.split(",");
                $.each(authorities1,function (i,item) {
                    authorities.push({
                        authority:item
                    })
                })
            }

            data['authorities'] =authorities;

            $.ajax({
                url: "/user/modifyUser",//这个就是请求地址对应sAjaxSource
                data: JSON.stringify(data),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                //async : false,
                success: function (result) {   //后台执行成功的回调函数
                    if (result && result.code == '1') {
                        new MsgModal("提示", "执行成功!", function () {
                            $('#modifyModal').modal('hide');
                            datatable.draw();
                        });

                    } else {
                        new MsgModal("提示", "执行失败!", function () {
                            $('#modifyModal').modal('hide');
                        });
                    }

                }
            });

        });

        function checkNode(data) {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            var nodes = zTree.transformToArray(zTree.getNodes());
            var str1="",str2="";
            $.each(nodes, function (index, treeNode) {
                zTree.checkNode(treeNode, false, null, false);
                return true;
            })

            $.each(data, function (index, item) {
                $.each(nodes, function (index, treeNode) {
                    if (treeNode.id == item.authority) {
                        zTree.checkNode(treeNode, true, null, false);
                        return false;
                    }
                })
            });
        }



        var setting = {
            check: {
                enable: true,
                chkStyle: "checkbox",
                radioType: "all"
            },
            chkboxType: {"Y": "", "N": ""},
            view: {
                dblClickExpand: false
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            callback: {
                onClick: beforeClick,
                onCheck: onCheck
            }
        };
        function beforeClick(e, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            zTree.checkNode(treeNode, !treeNode.checked, null, true);
            return false;
        }
        function onCheck(e, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
                nodes = zTree.getCheckedNodes(true),
                v = "",v1="";
            for (var i = 0, l = nodes.length; i < l; i++) {
                if(nodes[i].name&&!nodes[i].name.endsWith("模块")){
                    v += nodes[i].id + ",";
                    v1+=nodes[i].name + ",";
                }
            }
            if (v.length > 0) v = v.substring(0, v.length - 1);
            if (v1.length > 0) v1 = v1.substring(0, v1.length - 1);
            $("form [name=authorities1]").val(v ? v : "");
            $("form [name=authorities]").val(v1 ? v1 : "");
        }
        $.ajax({
            url: "/listAllModules",//这个就是请求地址对应sAjaxSource
            data: JSON.stringify({}),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
            type: 'post',
            dataType: 'json',
            contentType: "application/json",
            async: false,
            success: function (result) {
                if(!result) return;
                var zNodes = [];
                $.each(result,function(i,data){
                    if (data) {
                        zNodes.push({
                            id: data.moduleName,
                            pId: data.superModuleName,
                            name: data.moduleName&&!data.moduleName.endsWith("模块")?data.moduleName+"模块":data.moduleName,
                            open: true,
                            data: data
                        });
                        if(data.auths){
                            $.each(data.auths,function(i,item){
                                if(item){
                                    zNodes.push({
                                        id: item.authority,
                                        pId: item.moduleName,
                                        name: item.authorityName,
                                        open: true,
                                        checked:false,
                                        data: item
                                    });
                                }
                            })
                        }
                    }
                });
                $.fn.zTree.init($("#treeDemo"), setting, zNodes);
            }})


    });
