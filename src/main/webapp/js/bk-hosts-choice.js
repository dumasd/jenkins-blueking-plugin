let bkHostsTable;
let fileHostsTable;

function onChoiceTabClick(id) {
    let bkChoicePans = document.getElementById("bkChoicePans");
    let bkChoiceTabs = document.getElementById("bkChoiceTabs");

    for (const child of bkChoicePans.children) {
        if (child.id === "bkChoicePan_" + id) {
            child.style.display = "block";
        } else {
            child.style.display = "none";
        }
    }

    for (const tab of bkChoiceTabs.children) {
        if (tab.id === "bkChoiceTab_" + id) {
            tab.classList.add("active");
        } else {
            tab.classList.remove("active");
        }
    }
}

function toggleHostTableSingle(cb, table, selectedHostsInput) {
    const rows = table.config.store.state.data.rows;
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i]
        if (row.id === cb.id) {
            row.cells[0].data = cb.checked
            onHostSelection(row, selectedHostsInput)
            break;
        }
    }
}

function toggleHostTableAll(cb, table, selectedHostsInput) {
    const rows = table.config.store.state.data.rows;
    let selectedHostsData = JSON.parse(selectedHostsInput.value);
    let selectedHosts = []
    if (selectedHostsData.data) {
        selectedHosts = JSON.parse(selectedHostsData.data)
    }
    const hostIds = []
    for (let i = 0; i < rows.length; i++) {
        hostIds.push(rows[i].cells[1].data)
    }
    selectedHosts = selectedHosts.filter(el => hostIds.indexOf(el[3]) < 0)

    for (let i = 0; i < rows.length; i++) {
        const row = rows[i]
        row.cells[0].data = cb.checked
        const singleInput = document.getElementById(row.id)
        singleInput.checked = cb.checked

        const hostId = row.cells[1].data
        const innerIp = row.cells[2].data
        const outerIp = row.cells[3].data
        const name = row.cells[4].data

        if (cb.checked) {
            selectedHosts.push([
                innerIp, // inner
                outerIp, // outer
                name,  // name
                hostId
            ])
        }
    }
    selectedHostsData.data = selectedHosts
    selectedHostsInput.value = JSON.stringify(selectedHostsData)
    console.log(selectedHostsInput.value)
}


// ====================== Hosts from bk =====================
function toggleNode(event) {
    const node = event.currentTarget.closest('.tree-node');
    if (node.classList.toggle('collapsed')) {
        event.currentTarget.classList.remove('icon-angle-double-down')
        event.currentTarget.classList.add('icon-angle-double-right')
    } else {
        event.currentTarget.classList.remove('icon-angle-double-right')
        event.currentTarget.classList.add('icon-angle-double-down')
    }
}

function onClickNode(event) {
    const bkObjId = event.currentTarget.children[0].value
    const bkInstId = event.currentTarget.children[1].value
    const selectedBkObjIdInput = document.getElementById("selectedBkObjId")
    const selectedBkInstIdInput = document.getElementById("selectedBkInstId")

    const querySupported = (bkObjId === "biz" || bkObjId === "set" || bkObjId === "module")
    if (!querySupported) {
        return
    }

    selectedBkObjIdInput.value = bkObjId
    selectedBkInstIdInput.value = bkInstId
    const treeNodes = document.querySelectorAll(".tree-node-a")
    treeNodes.forEach((e, idx) => {
        if (event.currentTarget === e) {
            e.classList.add('tree-node-a-active')
        } else {
            e.classList.remove('tree-node-a-active')
        }
    })
    bkHostsTable.updateConfig({}).forceRender();
}

const fetchBkHosts = (opts) => {
    // console.log(opts)
    const bkHostSearchInput = document.getElementById("bkHostSearchInput")
    const selectedBkObjIdInput = document.getElementById("selectedBkObjId")
    const selectedBkInstIdInput = document.getElementById("selectedBkInstId")
    const selectedHostsInput = document.getElementById("selectedBkHosts")
    const bkHostsChoiceParams = document.getElementById("bkHostsChoiceParams").value
    return fetch(opts.url, {
        method: "post",
        headers: crumb.wrap({
            "Content-Type": "application/x-www-form-urlencoded"
        }),
        body: objectToUrlFormEncoded({
            bkHostsChoiceParams: bkHostsChoiceParams,
            selectedHosts: selectedHostsInput.value,
            bkObjId: selectedBkObjIdInput.value,
            bkInstId: selectedBkInstIdInput.value,
            keyword: bkHostSearchInput.value
        })
    }).then(response => response.json())
        .then(resp => {
            return {data: resp.info, total: resp.count};
        });
}


function onHostSelection(row, selectedHostsInput) {
    let selectedHostsData = JSON.parse(selectedHostsInput.value);
    let selectedHosts = []
    const hostId = row.cells[1].data
    const innerIp = row.cells[2].data
    const outerIp = row.cells[3].data
    const name = row.cells[4].data
    if (selectedHostsData.data) {
        selectedHosts = JSON.parse(selectedHostsData.data)
        selectedHosts = selectedHosts.filter(el => {
            return !(el[3] === outerIp || el[0] === innerIp);
        })
    }

    if (row.cells[0].data) {
        selectedHosts.push([
            innerIp, // inner
            outerIp, // outer
            name,  // name
            hostId
        ])
    }
    selectedHostsData.data = selectedHosts
    selectedHostsInput.value = JSON.stringify(selectedHostsData)
    console.log(selectedHostsInput.value)
}

function toggleBkHostAll(cb) {
    toggleHostTableAll(cb, bkHostsTable, document.getElementById("selectedBkHosts"));
}

function toggleBkHostSingle(cb) {
    toggleHostTableSingle(cb, bkHostsTable, document.getElementById("selectedBkHosts"));
}


// ====================== Hosts from file =====================

function fetchFileHosts(opts) {
    const fileHostSearchInput = document.getElementById("fileHostSearchInput")
    const bkHostsChoiceParams = document.getElementById("bkHostsChoiceParams").value
    const selectedHostsInput = document.getElementById("selectedFileHosts")
    return fetch(opts.url, {
        method: "post",
        headers: crumb.wrap({
            "Content-Type": "application/x-www-form-urlencoded"
        }),
        body: objectToUrlFormEncoded({
            bkHostsChoiceParams: bkHostsChoiceParams,
            selectedHosts: selectedHostsInput.value,
            keyword: fileHostSearchInput.value
        })
    }).then(response => response.json())
        .then(resp => {
            return {data: resp.info, total: resp.count};
        });
}

function toggleFileHostAll(cb) {
    toggleHostTableAll(cb, fileHostsTable, document.getElementById("selectedFileHosts"));
}

function toggleFileHostSingle(cb) {
    toggleHostTableSingle(cb, fileHostsTable, document.getElementById("selectedFileHosts"));
}

document.addEventListener('DOMContentLoaded', function () {
    const BASE_URL = document.getElementById("bkHostsChoiceBaseUrl").value;
    const bkHostsTableDiv = document.getElementById("bkHostsTable")
    if (bkHostsTableDiv) {
        bkHostsTable = new gridjs.Grid({
            columns: [
                {
                    id: 'selected',
                    name: gridjs.html('<input id="bkHostAllCheckbox" type="checkbox" onclick="toggleBkHostAll(this);"/>'),
                    data: (row) => row.selected,
                    formatter: (cell, row, column) => {
                        var checked = cell ? "checked" : ""
                        return gridjs.html('<input id="' + row.id + '" type="checkbox" onclick="toggleBkHostSingle(this);" ' + checked + ' />')
                    }
                },
                {
                    id: 'bk_host_id',
                    name: 'ID'
                },
                {
                    id: 'bk_host_innerip',
                    name: 'Inner IP'
                },
                {
                    id: 'bk_host_outerip',
                    name: 'Outer IP'
                },
                {
                    id: 'host_name',
                    name: 'Host Name'
                }
            ],
            fixedHeader: true,
            height: '500px',
            server: {
                url: BASE_URL + "/searchBkHosts",
                data: (opts) => {
                    return fetchBkHosts(opts);
                }
            },
            pagination: {
                limit: 100,
                server: {
                    url: (prev, page, limit) => {
                        return prev + "?limit=" + limit + "&page=" + page;
                    }
                }
            }
        }).render(bkHostsTableDiv);

        bkHostsTable.config.store.subscribe(function (state) {
            if (state.status === 2) {
                const bkHostAllCheckbox = document.getElementById("bkHostAllCheckbox")
                const rows = state.data.rows;
                let allCheckboxChecked = rows.length > 0;
                for (let i = 0; i < rows.length; i++) {
                    const row = rows[i]
                    if (!row.cells[0].data) {
                        allCheckboxChecked = false;
                        break;
                    }
                }
                bkHostAllCheckbox.checked = allCheckboxChecked;
            }
        })

        const bkHostSearchInput = document.getElementById("bkHostSearchInput");
        bkHostSearchInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                console.log("回车键被按下，输入的值：", bkHostSearchInput.value);
                // 搜索
                bkHostsTable.forceRender();
                // 可选：阻止默认行为（如在某些表单中，回车会触发提交）
                event.preventDefault();
            }
        });
    }

    const fileHostsTableDiv = document.getElementById("fileHostsTable")
    if (fileHostsTableDiv) {
        fileHostsTable = new gridjs.Grid({
            columns: [
                {
                    id: 'selected',
                    name: gridjs.html('<input id="fileHostAllCheckbox" type="checkbox" onclick="toggleFileHostAll(this);"/>'),
                    data: (row) => row.selected,
                    formatter: (cell, row, column) => {
                        var checked = cell ? "checked" : ""
                        return gridjs.html('<input id="' + row.id + '" type="checkbox" onclick="toggleFileHostSingle(this);" ' + checked + ' />')
                    }
                },
                {
                    id: 'host_id',
                    name: 'ID'
                },
                {
                    id: 'host_innerip',
                    name: 'Inner IP'
                },
                {
                    id: 'host_outerip',
                    name: 'Outer IP'
                },
                {
                    id: 'host_name',
                    name: 'Host Name'
                },
                {
                    id: 'module',
                    name: 'Module'
                }
            ],
            fixedHeader: true,
            height: '500px',
            server: {
                url: BASE_URL + "/searchFileHosts",
                data: (opts) => {
                    return fetchFileHosts(opts);
                }
            },
            pagination: {
                limit: 100,
                server: {
                    url: (prev, page, limit) => {
                        return prev + "?limit=" + limit + "&page=" + page;
                    }
                }
            }
        }).render(fileHostsTableDiv);

        fileHostsTable.config.store.subscribe(function (state) {
            if (state.status === 2) {
                const bkHostAllCheckbox = document.getElementById("fileHostAllCheckbox")
                const rows = state.data.rows;
                let allCheckboxChecked = rows.length > 0;
                for (let i = 0; i < rows.length; i++) {
                    const row = rows[i]
                    if (!row.cells[0].data) {
                        allCheckboxChecked = false;
                        break;
                    }
                }
                bkHostAllCheckbox.checked = allCheckboxChecked;
            }
        })

        const fileHostSearchInput = document.getElementById("fileHostSearchInput");
        fileHostSearchInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                console.log("回车键被按下，输入的值：", fileHostSearchInput.value);
                // 搜索
                fileHostsTable.forceRender();
                // 可选：阻止默认行为（如在某些表单中，回车会触发提交）
                event.preventDefault();
            }
        });
    }

});
