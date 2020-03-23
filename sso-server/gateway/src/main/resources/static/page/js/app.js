function dataTableInit(selector, url, columns, columnDefs, buttons, formSelector, retrieveData1, dataCallback) {

    if (console.log("run_datatables"), "undefined" != typeof $.fn.DataTable) {
        console.log("init_DataTables");
        var retrieveData2
        if (retrieveData1) {
            retrieveData2 = retrieveData1
        } else {
            retrieveData2 = retrieveData
        }
        var b = $(selector).DataTable({
            dom: "Bfrtip",
            searching: false,
            "serverSide": true,
            "ajaxSource": url,
            "columnDefs": columnDefs,
            "serverData": retrieveData2, // 获取数据的处理函数
            "columns": columns,
            buttons: buttons,
            select: true,
            //  'order': [[ 1, 'asc' ]],
            language: {
                "sProcessing": "处理中...",
                "sLengthMenu": "显示 _MENU_ 项结果",
                "sZeroRecords": "没有匹配结果",
                "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
                "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
                "sInfoFiltered": "",
                //"sInfoFiltered": "(由 _MAX_ 项结果过滤)",
                "sInfoPostFix": "",
                "sSearch": "搜索:",
                "sUrl": "",
                "sEmptyTable": "表中数据为空",
                "sLoadingRecords": "载入中...",
                "sInfoThousands": ",",
                "oPaginate": {
                    "sFirst": "首页",
                    "sPrevious": "上页",
                    "sNext": "下页",
                    "sLast": "末页"
                },
                "oAria": {
                    "sSortAscending": ": 以升序排列此列",
                    "sSortDescending": ": 以降序排列此列"
                }
            }
        });
        b.on("draw.dt", function () {
            $("input.flat").iCheck({checkboxClass: "icheckbox_flat-green"});

            $(".bulk_action input").off("ifChecked").on("ifChecked", function () {
                if (!$(this).parent().parent().parent().hasClass('selected')) {
                    checkState = "", $(this).parent().parent().parent().addClass("selected"), countChecked();
                    b.rows($(this).parent().parent().parent()).select();
                }


            });
            $(".bulk_action input").off("ifUnchecked").on("ifUnchecked", function () {
                if ($(this).parent().parent().parent().hasClass('selected')) {
                    b.rows($(this).parent().parent().parent()).deselect();
                    checkState = "", $(this).parent().parent().parent().removeClass("selected"), countChecked()
                }


            });
            $(".bulk_action input#check-all").off("ifChecked").on("ifChecked", function () {
                checkState = "all", countChecked()
                b.rows(".selected").select();
            });
            $(".bulk_action input#check-all").off("ifUnchecked").on("ifUnchecked", function () {
                b.rows(".selected").deselect();
                checkState = "none", countChecked()

            });
        });

        b.on('select', function (e, dt, type, indexes) {
            $.each(indexes, function (index, item) {
                b[type](item).nodes().to$().find(".icheckbox_flat-green input").iCheck('check');
                checkState = "";
                countChecked();
            })


        });
        b.on('deselect', function (e, dt, type, indexes) {
            $.each(indexes, function (index, item) {

                b[type](item).nodes().to$().find(".icheckbox_flat-green input").iCheck('uncheck');
                checkState = "";
                countChecked();
            });


        });


        return b;
    }

    function retrieveData(sSource, aoData, fnCallback) {
        var sEcho;
        var iDisplayStart;
        var iDisplayLength;
        $.each(aoData, function (index, item) {
            if (item.name == "sEcho") {
                sEcho = item.value;
            }
            if (item.name == "iDisplayLength") {
                iDisplayLength = item.value;
            }
            if (item.name == "iDisplayStart") {
                iDisplayStart = item.value;
            }

        });

        var page = {
            pageSize: iDisplayLength,
            pageNo: (iDisplayStart / iDisplayLength + 1)
        }

        if (formSelector) {
            var jsonCondition = $(formSelector).serializeObject();
        }
        if (jsonCondition) {
            $.extend(page, jsonCondition);
        }
        $.ajax({
            url: sSource,//这个就是请求地址对应sAjaxSource
            data: JSON.stringify(page),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
            type: 'post',
            dataType: 'json',
            contentType: "application/json",
            beforeSend: function (XMLHttpRequest) {
                var an = localStorage.getItem("Authorization");
                XMLHttpRequest.setRequestHeader("Authorization", an);

            },
            //async : false,
            success: function (result) {   //后台执行成功的回调函数
                var o = {
                    sEcho: sEcho,
                    iToff8080816c125cf0016c125d214c000atalRecords: result.count,
                    iTotalDisplayRecords: result.count,
                    iDisplayStart: iDisplayStart,
                    iDisplayLength: iDisplayLength,
                    aaData: result.list
                }
                if (o.aaData) {
                    dataCallback(o.aaData)
                }
                fnCallback(o);//把返回的数据传给这个方法就可以了,datatable会自动绑定数据的
            },
            error: function (msg) {
            }
        });
    }
}

function selectInit(selector, url, callback) {
    $.ajax({
        url: url,//这个就是请求地址对应sAjaxSource
        data: JSON.stringify({
            pageNo: 0,
            pageSize: 0
        }),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
        type: 'post',
        dataType: 'json',
        contentType: "application/json",
        async: false,
        success: function (result) {   //后台执行成功的回调函数
            var html = ['<option value="">请选择</option>'];

            if (result && result.list) {
                $.each(result.list, function (index, element) {
                    html.push(callback(element));
                })
            }
            $(selector).html(html.join(""));

        },
        error: function (msg) {
        }
    });
    return $(selector);
}

function logout() {
    $.ajax({
        url: preffix+"/logout",//这个就是请求地址对应sAjaxSource
        data: JSON.stringify({}),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
        type: 'post',
        dataType: 'json',
        contentType: "application/json",
        async: false,
        success: function (result) {   //后台执行成功的回调函数


        },
        error: function (msg) {
        }
    });
}

/**
 * form表单序列化方法
 */
$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

var daterangconfig = {
    locale: {
        format: 'YYYY/MM/DD',
        applyLabel: '确定',
        cancelLabel: '取消',
        fromLabel: '起始时间',
        toLabel: '结束时间',
        daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
        monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
        firstDay: 1
    },
    separator: '-',
    startDate: new Date(),
    endDate: new Date()
};

if ($('#sidebar-menu').length > 0) {
    // $.ajax({
    //     url: "/menu/list",//这个就是请求地址对应sAjaxSource
    //     data: JSON.stringify({
    //         pageNo: 0,
    //         pageSize: 0
    //     }),//这个是把datatable的一些基本数据传给后台,比如起始位置,每页显示的行数
    //     type: 'post',
    //     dataType: 'json',
    //     contentType: "application/json",
    //     //async : false,
    //     success: function (result) {   //后台执行成功的回调函数
    //         if (result && result.list) {
    //             var datas = result.list;
    //             var root = makeTree(datas);
    //             listMenu(root);
    //         }
    //     }
    // });
    var root = makeTree([
        {
            "id": "1",
            "resourceName": "模块管理",
            "superMenuId": null,
            "menuName": "模块管理",
            "menuURI": "/page/module.html",
            "order": 1,
            "superMenu": null,
            "childrenMenu": null
        },
        {
            "id": "2",
            "resourceName": "用户管理",
            "superMenuId": null,
            "menuName": "用户管理",
            "menuURI": "/page/user.html",
            "order": 2,
            "superMenu": null,
            "childrenMenu": null
        },
    ])
    listMenu(root);
}

function listMenu(root) {
    var html = "";

    function addHtml(node, flag) {
        var html = "";
        if (node.children) {
            html = '<ul class="nav child_menu active">';

            for (var i = 0, length = node.children.length; i < length; i++) {
                var child = node.children[i];
                if (child) {
                    var subMenu = "";
                    if (!flag) {
                        subMenu = 'class="sub_menu"'
                    }
                    var href = 'href="' + child.data.menuURI + '"';
                    if (child.children && child.children.length > 0) {
                        href = 'href="javascript:void(0);"';
                    }
                    html += '<li ' + subMenu + '><a ' + href + '><i ></i> ' + child.data.menuName;
                    if (child.children && child.children.length > 0) {
                        html += '<span class="fa fa-chevron-down"></span>';
                    }
                    html += '</a>';
                    html += addHtml(child);
                    html += '</li>'
                }

            }
            html += '</ul>';
        }

        return html;

    }

    if (root && root.children && root.children.length > 0) {
        html = '<ul class="nav side-menu active">';
        for (var i = 0, length = root.children.length; i < length; i++) {
            var child = root.children[i];
            if (child) {
                var href = 'href="' + child.data.menuURI + '"';
                if (child.children && child.children.length > 0) {
                    href = 'href="javascript:void(0);"';
                }
                html += '<li><a ' + href + '><i class="fa fa-sitemap"></i> ' + child.data.menuName;
                if (child.children && child.children.length > 0) {
                    html += '<span class="fa fa-chevron-down"></span>';
                }
                html += '</a>';
                html += addHtml(child, true);
                html += '</li>'
            }
        }
        html += '</ul>';
    }
    $("#sidebar-menu .menu_section").html(html);
    init_sidebar();

}

function makeTree(datas) {
    var Node = function (data) {
        this.data = data;
    }
    Node.prototype.appendChild = function (child) {
        if (!this.children) {
            this.children = [];
        }
        this.children.push(child);
    }
    var root = new Node({id: "root", menuName: "根菜单", url: "url", superMenuId: null});
    if (datas) {
        var nodes = [];
        for (var i = 0, length = datas.length; i < length; i++) {
            var data = datas[i];
            if (data) {
                nodes.push(new Node(data));
            }
        }

        function addNodes(nodes) {
            var temp = [], temp1 = [];
            while (nodes.length > 0) {
                var node = nodes.splice(0, 1)[0];
                if (node.data.superMenuId == null) {
                    temp.push(node);
                    root.appendChild(node);
                } else {
                    temp1.push(node);
                }

            }
            while (temp1.length > 0) {
                var node = temp1.splice(0, 1)[0];
                var flag = false;
                for (var i = 0, size = temp.length; i < size; i++) {
                    var child = temp[i];
                    if (addNode(child, node)) {
                        flag = true;
                        break;
                    }

                }
                if (!flag) {
                    for (var i = 0, size = temp1.length; i < size; i++) {
                        var child = temp1[i];
                        if (addNode(child, node)) {
                            flag = true;
                            break;
                        }

                    }
                }
                if (!flag) {
                    root.appendChild(node);
                    temp.push(node);
                }
            }
        }

        function addNode(node1, node2) {
            if (node2.data.superMenuId == node1.data.id) {
                node1.appendChild(node2);
                return true;
            } else {
                var children = node1.children;
                if (children) {
                    for (var i = 0, length = children.length; i < length; i++) {
                        var child = children[i];
                        if (child) {
                            var b = addNode(child, node2);
                            if (b) {
                                return true;
                            }
                        }
                    }
                }
            }

        }

        addNodes(nodes)
    }
    return root;
}

Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "H+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "S+": this.getMilliseconds()
    };
    //因为date.getFullYear()出来的结果是number类型的,所以为了让结果变成字符串型，下面有两种方法：
    if (/(y+)/.test(fmt)) {
        //第一种：利用字符串连接符“+”给date.getFullYear()+""，加一个空字符串便可以将number类型转换成字符串。
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            //第二种：使用String()类型进行强制数据类型转换String(date.getFullYear())，这种更容易理解。
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(String(o[k]).length)));
        }
    }
    return fmt;
};


$.ajaxSetup({
    beforeSend: function (xhr) {
        $("#div_brg0001").show();
        var an = localStorage.getItem("Authorization");
        xhr.setRequestHeader("Authorization", an);
    },
    complete: function (XMLHttpRequest, textStatus) {
        var Authorization = XMLHttpRequest.getResponseHeader("Authorization")
        if (Authorization && (!localStorage.getItem("Authorization") || localStorage.getItem("Authorization") && Authorization != localStorage.getItem("Authorization"))) {
            localStorage.setItem("Authorization", Authorization);

        }
        // 通过XMLHttpRequest取得响应头，REDIRECT
        var redirect = XMLHttpRequest.getResponseHeader("REDIRECT");
        //若HEADER中含有REDIRECT说明后端想重定向
        if (redirect == "REDIRECT") {
            var win = window;
            while (win != win.top) {
                win = win.top;
            }
            win.location.href = XMLHttpRequest.getResponseHeader("CONTEXTPATH");
        }
        failHandler(XMLHttpRequest.status, textStatus);
        $("#div_brg0001").hide();
    },
    error: function (jqXHR, textStatus, errorThrown) {
        failHandler(jqXHR.status);

    }
});

function failHandler(status, error) {
    switch (status) {
        case(500):
            new MsgModal("提示", "服务器系统内部错误");
            window.location.href = "/login"
            break;
        case(401):
            new MsgModal("提示", "无权限执行此操作");
            window.location.href = "/login"
            break;
        case(403):
            new MsgModal("提示", "无权限执行此操作");
            window.location.href = "/login"
            break;
        case(408):
            new MsgModal("提示", "请求超时");
            break;
        default:
            if (error == "error")
                new MsgModal("提示", "未知错误");
    }
}

$("<div id='div_brg0001'></div>").css({
    position: 'absolute',
    top: 0,
    left: 0,
    backgroundColor: "#A2A2A2",
    opacity: 0.1,
    zIndex: 300
})
    .height($(document).height())
    .width($(document).width()).hide().appendTo("body")
$(document).ready(
    function () {
        try {
            if ($("form [name=moduleId]").length > 0) {
                selectInit("form [name=moduleId]", "/module/list", function (element) {
                    if (element) {
                        return '<option value="' + (element.id || '') + '">' + (element.moduleName || '') + '</option>';
                    }
                })
            }

        } catch (e) {

        }

    });

function MsgModal(title, text, callback) {
    if ($(".msgModal").length > 0) {
        $("#myMsgModalLabel").text((title || ""));
        $("#msgText").text((text || ""));

    } else {

        $("body").append('<div class="modal fade msgModal"  tabindex="-1" role="dialog" aria-labelledby="myMsgModalLabel" aria-hidden="true">\n' +
            '                                    <div class="modal-dialog">\n' +
            '                                        <div class="modal-content">\n' +
            '                                            <div class="modal-header">\n' +
            '                                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">\n' +
            '                                                    &times;\n' +
            '                                                </button>\n' +
            '                                                <h4 class="modal-title" id="myModalLabel">\n' + (title || "") +
            '                                                </h4>\n' +
            '                                            </div>\n' +
            '                                            <div id="msgText" class="modal-body">\n' + (text || "") +
            '                                            </div>\n' +
            '                                            <div class="modal-footer">\n' +
            '                                                <button type="button" class="btn btn-primary" data-dismiss="modal">关闭\n' +
            '                                                </button>\n' +
            '                                                <button type="button" class="btn btn-success msgConfirm" >\n' +
            '                                                    确定\n' +
            '                                                </button>\n' +
            '                                            </div>\n' +
            '                                        </div>\n' +
            '                                    </div>\n' +
            '                                </div>');

    }
    $('.msgConfirm').unbind('click').on("click", function () {
        if (callback) {
            callback();
        }
        $(".msgModal").modal('hide');

    });

    $(".msgModal").modal('show');
    return $(".msgModal");
}

var preffix="/api";
//new MsgModal('提示','确定删除数据！');