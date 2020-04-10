$(document).ready(
    function () {
        var setting = {
            check: {
                enable: true
            },
            data: {
                simpleData: {
                    enable: true
                }
            }, callback: {
                onClick: onClick1,
                onCheck: onCheck1
            }
        };
        var setting1 = {
            check: {
                enable: true,
                chkStyle: "radio",
                radioType: "all"
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
                onClick: onClick,
                onCheck: onCheck
            }
        };

        function onClick(e, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo1");
            zTree.checkNode(treeNode, !treeNode.checked, null, true);
            return false;
        }

        function onCheck(e, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo1"),
                nodes = zTree.getCheckedNodes(true),
                v = "";
            for (var i = 0, l = nodes.length; i < l; i++) {
                v += nodes[i].data.menuName + ",";
            }
            if (v.length > 0) v = v.substring(0, v.length - 1);
            var cityObj = $("form [name=superMenuName]");
            cityObj.val(v);
            $("form [name=superMenuId]").val(treeNode.data.id);
        }
        function onCheck1(e, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
                nodes = zTree.getCheckedNodes(true),
                v = "";
            for (var i = 0, l = nodes.length; i < l; i++) {
                if(nodes[i].id){
                    v += nodes[i].id + ",";
                }

            }
            if (v.length > 0) v = v.substring(0, v.length - 1);
            $("form [name=id]").prop("value",v);
        }
        function showMenu() {
            var cityObj = $("form [name=superMenuName]");
            var cityOffset = $("form [name=superMenuName]").offset();
            $("#menuContent").css({
                left: cityOffset.left + "px",
                top: cityOffset.top + cityObj.outerHeight() + "px"
            }).slideDown("fast");

            $("body").bind("mousedown", onBodyDown);
        }

        function hideMenu() {
            $("#menuContent").fadeOut("fast");
            $("body").unbind("mousedown", onBodyDown);
        }

        function onBodyDown(event) {
            if (!(event.target.id == "menuBtn" || event.target.id == "citySel" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length > 0)) {
                hideMenu();
            }
        }

        function onClick1(event, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            zTree.checkNode(treeNode, !treeNode.checked, null, true);

            if (treeNode) {
                if (treeNode.id) {
                    $("form [name=mode]").prop("value","MODIFY");
                    $("form [name=id]").prop("value",treeNode.data.id);
                    initMenu1();
                    $("form [name=menuName]").prop("value",treeNode.data.menuName);
                    $("form [name=menuURI]").prop("value",treeNode.data.menuURI);
                    $("form [name=order]").prop("value",treeNode.data.order);
                    $("form [name=moduleId]").find("option").prop("selected",false);
                    if(treeNode.data.module){
                        $("form [name=moduleId]").find('[value="'+treeNode.data.module.id+'"]').prop("selected",true);
                    }
                    var node = treeNode.getParentNode();
                    if (node && node.id) {
                        $("form [name=superMenuName]").val(node.data.menuName);
                    } else {
                        $("form [name=superMenuName]").val("");
                    }
                    $("form [name=superMenuId]").val(treeNode.data.superMenuId ? treeNode.data.superMenuId : "");

                } else {
                    $("form")[0].reset();

                }
            }
            console.log(JSON.stringify(treeNode));
            return false;
        }

        $("#add").on("click", function () {
            $("form")[0].reset();
            $("form [name=id]").prop("value","");
            $("form [name=mode]").prop("value","ADD");
            initMenu1();

        });
        $("#del").on("click", function () {
            $("form [name=mode]").prop("value","DROP");
            onCheck1();
            formSubmit();
        });
        $("form [name=superMenuName]").on("click",showMenu);
        function setCheck() {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            zTree.setting.check.chkboxType = {"Y": "ps", "N": "s"};
        }

        function initMenu() {
            $.ajax({
                url: "/menu/list",//这个就是请求地址对应sAjaxSource
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
                        var zNodes = [{id: null, pId: null, name: '根菜单'}];

                        for (var i = 0, length = datas.length; i < length; i++) {
                            var data = datas[i];
                            if (data) {
                                zNodes.push({
                                    id: data.id,
                                    pId: data.superMenuId,
                                    name: data.menuName,
                                    data: data
                                });
                            }
                        }


                        $.fn.zTree.init($("#treeDemo"), setting, zNodes);
                        $.fn.zTree.init($("#treeDemo1"), setting1, zNodes);
                        setCheck();
                    }
                }
            });
        }
        function initMenu1() {
            $.ajax({
                url: "/menu/list",//这个就是请求地址对应sAjaxSource
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
                        var zNodes = [{id: null, pId: null, name: '根菜单'}];

                        for (var i = 0, length = datas.length; i < length; i++) {
                            var data = datas[i];
                            if (data) {
                                zNodes.push({
                                    id: data.id,
                                    pId: data.superMenuId,
                                    name: data.menuName,
                                    data: data,
                                    chkDisabled:data.id==$("form [name=id]").val()
                                });
                            }
                        }
                        $.fn.zTree.init($("#treeDemo1"), setting1, zNodes);
                    }
                }
            });
        }
        initMenu();

        function formSubmit(e){
            if(e){
                e.preventDefault();
            }

            var json = $("form").serializeObject();
            $.ajax({
                url: "/menu/maintenance",//这个就是请求地址对应sAjaxSource
                data: JSON.stringify(json),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                success: function (result) {
                    if (result && result.code == '1') {
                        alert("执行成功!");
                        initMenu();
                        $("form")[0].reset();
                    }
                }
            });
        }
        $("form").submit(formSubmit);
    });
