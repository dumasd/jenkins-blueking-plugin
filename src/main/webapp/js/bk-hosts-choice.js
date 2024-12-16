let bkHostsTable;
let fileHostsTable;
let selectedHostsTable;

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

function getSelectHostArray(selectedHostsData) {
    if (selectedHostsData.data) {
        if (Array.isArray(selectedHostsData.data)) {
            return selectedHostsData.data
        } else if (typeof (selectedHostsData.data) === 'string') {
            return JSON.parse(selectedHostsData.data)
        }
    }
    return []
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
    let selectedHosts = getSelectHostArray(selectedHostsData);

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

function initHostTableCheckBox(state, cb) {
    const rows = state.data.rows;
    let allCheckboxChecked = rows.length > 0;
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i]
        if (!row.cells[0].data) {
            allCheckboxChecked = false;
            break;
        }
    }
    cb.checked = allCheckboxChecked;
}

function clearHostTableSelected(table, selectedHostsInput, allCheckBox) {
    const rows = table.config.store.state.data.rows;
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i]
        row.cells[0].data = false
        const singleCheckbox = document.getElementById(row.id)
        singleCheckbox.checked = false
    }
    allCheckBox.checked = false
    selectedHostsInput.value = "{}"
}


// ====================== Hosts from bk =====================
function toggleNode(event) {
    const collapsed = event.currentTarget.classList.toggle('collapsed');
    const parentLi = event.currentTarget.parentNode;
    const last = parentLi.lastChild;
    if (last.tagName.toLowerCase() === 'ul') {
        if (collapsed) {
            last.style.display = 'block';
        } else {
            last.style.display = 'none';
        }
    }
    event.stopPropagation();
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
    const treeNodes = document.querySelectorAll(".tree li a.active")
    treeNodes.forEach((e, idx) => {
        e.classList.remove('active')
    })
    event.currentTarget.classList.add('active')
    bkHostsTable.forceRender()
}

const fetchBkHosts = (opts) => {
    // console.log(opts)
    const choiceParams = document.getElementById("bkHostsChoiceParams").value
    const searchInput = document.getElementById("bkHostSearchInput")
    const selectedBkObjIdInput = document.getElementById("selectedBkObjId")
    const selectedBkInstIdInput = document.getElementById("selectedBkInstId")
    const selectedHostsInput = document.getElementById("selectedBkHosts")
    return fetch(opts.url, {
        method: "post",
        headers: crumb.wrap({
            "Content-Type": "application/x-www-form-urlencoded"
        }),
        body: objectToUrlFormEncoded({
            bkHostsChoiceParams: choiceParams,
            selectedHosts: selectedHostsInput.value,
            bkObjId: selectedBkObjIdInput.value,
            bkInstId: selectedBkInstIdInput.value,
            keyword: searchInput.value
        })
    }).then(response => response.json())
        .then(resp => {
            return {data: resp.info, total: resp.count};
        });
}


function onHostSelection(row, selectedHostsInput) {
    let selectedHostsData = JSON.parse(selectedHostsInput.value);
    let selectedHosts = getSelectHostArray(selectedHostsData);
    const hostId = row.cells[1].data
    const innerIp = row.cells[2].data
    const outerIp = row.cells[3].data
    const name = row.cells[4].data
    selectedHosts = selectedHosts.filter(el => {
        return !(el[3] === outerIp || el[0] === innerIp);
    })
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
    const choiceParams = document.getElementById("bkHostsChoiceParams").value
    const searchInput = document.getElementById("fileHostSearchInput")
    const selectedHostsInput = document.getElementById("selectedFileHosts")
    return fetch(opts.url, {
        method: "post",
        headers: crumb.wrap({
            "Content-Type": "application/x-www-form-urlencoded"
        }),
        body: objectToUrlFormEncoded({
            bkHostsChoiceParams: choiceParams,
            selectedHosts: selectedHostsInput.value,
            keyword: searchInput.value
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


// ===================== Selected hosts view ======================
function fetchSelectedHosts(opts) {
    const model = document.getElementById("selectedHostsViewModel").value;
    let selectedHosts = '{}';
    if (model === 'file') {
        selectedHosts = document.getElementById("selectedFileHosts").value
    } else {
        selectedHosts = document.getElementById("selectedBkHosts").value
    }
    const searchInput = document.getElementById("selectedHostSearchInput");
    return fetch(opts.url, {
        method: "post",
        headers: crumb.wrap({
            "Content-Type": "application/x-www-form-urlencoded"
        }),
        body: objectToUrlFormEncoded({
            selectedHosts: selectedHosts,
            keyword: searchInput.value
        })
    }).then(response => response.json())
        .then(resp => {
            return {data: resp.info, total: resp.count};
        });
}

function toggleSelectedHostAll(cb) {
    const rows = selectedHostsTable.config.store.state.data.rows;
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i]
        row.cells[0].data = cb.checked
        const singleInput = document.getElementById(row.id)
        singleInput.checked = cb.checked
    }
}

function toggleSelectedHostSingle(cb) {
    const rows = selectedHostsTable.config.store.state.data.rows;
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i]
        if (row.id === cb.id) {
            row.cells[0].data = cb.checked
            break
        }
    }
}


function deleteSelectedHosts() {
    const rows = selectedHostsTable.config.store.state.data.rows;
    const model = document.getElementById("selectedHostsViewModel").value;
    let selectedHostsInput;
    if (model === 'file') {
        selectedHostsInput = document.getElementById("selectedFileHosts")
    } else {
        selectedHostsInput = document.getElementById("selectedBkHosts")
    }
    let selectedHostsData = JSON.parse(selectedHostsInput.value);
    let selectedHosts = getSelectHostArray(selectedHostsData);
    const deleteHostIds = []
    for (let i = 0; i < rows.length; i++) {
        if (rows[i].cells[0].data) {
            deleteHostIds.push(rows[i].cells[1].data)
        }
    }

    if (deleteHostIds.length > 0) {
        selectedHosts = selectedHosts.filter(el => deleteHostIds.indexOf(el[3]) < 0)
        selectedHostsData.data = selectedHosts
        selectedHostsInput.value = JSON.stringify(selectedHostsData)
        console.log(selectedHostsInput.value)
        selectedHostsTable.forceRender();
        if (model === 'file') {
            fileHostsTable.forceRender();
        } else {
            bkHostsTable.forceRender();
        }
    }
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
                    width: '65px',
                    data: (row) => row.selected,
                    formatter: (cell, row, column) => {
                        let checked = cell ? "checked" : ""
                        return gridjs.html('<input id="' + row.id + '" type="checkbox" onclick="toggleBkHostSingle(this);" ' + checked + ' />')
                    }
                },
                {
                    id: 'bk_host_id',
                    name: 'ID',
                    width: '80px'
                },
                {
                    id: 'bk_host_innerip',
                    name: 'Inner IP',
                    width: '135px'
                },
                {
                    id: 'bk_host_outerip',
                    name: 'Outer IP',
                    width: '135px'
                },
                {
                    id: 'host_name',
                    name: 'Host Name'
                }
            ],
            fixedHeader: true,
            height: '520px',
            server: {
                url: BASE_URL + "/searchBkHosts",
                data: (opts) => {
                    return fetchBkHosts(opts);
                }
            },
            pagination: {
                limit: 200,
                server: {
                    url: (prev, page, limit) => {
                        return prev + "?limit=" + limit + "&page=" + page;
                    }
                }
            }
        }).render(bkHostsTableDiv);

        bkHostsTable.config.store.subscribe(function (state) {
            if (state.status === 2) {
                const allCheckbox = document.getElementById("bkHostAllCheckbox")
                initHostTableCheckBox(state, allCheckbox)
            }
        })

        const bkHostSearchInput = document.getElementById("bkHostSearchInput");
        bkHostSearchInput.addEventListener("keydown", event => {
            if (event.key === "Enter") {
                // 搜索
                bkHostsTable.forceRender();
                // 可选：阻止默认行为（如在某些表单中，回车会触发提交）
                event.preventDefault();
            }
        });

        // clear
        document.getElementById("bkHostClearBtn").addEventListener("click", event => {
            const allCheckbox = document.getElementById("bkHostAllCheckbox")
            const selectedFileHostInput = document.getElementById("selectedBkHosts")
            clearHostTableSelected(bkHostsTable, selectedFileHostInput, allCheckbox)
        })

        document.getElementById("bkHostViewBtn").addEventListener("click", event => {
            document.getElementById("selectedViewModal").classList.add("active")
            document.getElementById("selectedHostsViewModel").value = 'bkIp';
            selectedHostsTable.forceRender()
        })
    }

    const fileHostsTableDiv = document.getElementById("fileHostsTable")
    if (fileHostsTableDiv) {
        fileHostsTable = new gridjs.Grid({
            columns: [
                {
                    id: 'selected',
                    name: gridjs.html('<input id="fileHostAllCheckbox" type="checkbox" onclick="toggleFileHostAll(this);"/>'),
                    width: '65px',
                    data: (row) => row.selected,
                    formatter: (cell, row, column) => {
                        let checked = cell ? "checked" : ""
                        return gridjs.html('<input id="' + row.id + '" type="checkbox" onclick="toggleFileHostSingle(this);" ' + checked + ' />')
                    }
                },
                {
                    id: 'host_id',
                    name: 'ID',
                    width: '80px'
                },
                {
                    id: 'host_innerip',
                    name: 'Inner IP',
                    width: '135px'
                },
                {
                    id: 'host_outerip',
                    name: 'Outer IP',
                    width: '135px'
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
            height: '510px',
            server: {
                url: BASE_URL + "/searchFileHosts",
                data: (opts) => {
                    return fetchFileHosts(opts);
                }
            },
            pagination: {
                limit: 200,
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
                initHostTableCheckBox(state, bkHostAllCheckbox)
            }
        })

        const fileHostSearchInput = document.getElementById("fileHostSearchInput");
        fileHostSearchInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                // 搜索
                fileHostsTable.forceRender();
                // 可选：阻止默认行为（如在某些表单中，回车会触发提交）
                event.preventDefault();
            }
        });

        document.getElementById("fileHostClearBtn").addEventListener("click", event => {
            const allCheckbox = document.getElementById("fileHostAllCheckbox")
            const selectedFileHostInput = document.getElementById("selectedFileHosts")
            clearHostTableSelected(fileHostsTable, selectedFileHostInput, allCheckbox)
        })

        document.getElementById("fileHostViewBtn").addEventListener("click", event => {
            document.getElementById("selectedViewModal").classList.add("active")
            document.getElementById("selectedHostsViewModel").value = 'file';
            selectedHostsTable.forceRender()
        })
    }

    selectedHostsTable = new gridjs.Grid({
        columns: [
            {
                id: 'selected',
                name: gridjs.html('<input id="fileHostAllCheckbox" type="checkbox" onclick="toggleSelectedHostAll(this);"/>'),
                width: '65px',
                formatter: (cell, row, column) => {
                    return gridjs.html('<input id="' + row.id + '" type="checkbox" onclick="toggleSelectedHostSingle(this);"/>')
                }
            },
            {
                id: 'id',
                name: 'ID',
                width: '80px'
            },
            {
                id: 'innerip',
                name: 'Inner IP',
                width: '135px'
            },
            {
                id: 'outerip',
                name: 'Outer IP',
                width: '135px'
            },
            {
                id: 'name',
                name: 'Host Name'
            }
        ],
        fixedHeader: true,
        height: '500px',
        server: {
            url: BASE_URL + "/getSelectedHosts",
            data: (opts) => {
                return fetchSelectedHosts(opts);
            }
        },
        pagination: {
            limit: 200,
            server: {
                url: (prev, page, limit) => {
                    return prev + "?limit=" + limit + "&page=" + page;
                }
            }
        }
    }).render(document.getElementById("selectedHostsTable"));

    document.getElementById("deleteSelectedHost").addEventListener("click", () => {
        deleteSelectedHosts()
    })

    const selectedHostSearchInput = document.getElementById("selectedHostSearchInput");
    selectedHostSearchInput.addEventListener("keydown", event => {
        if (event.key === "Enter") {
            // 搜索
            selectedHostsTable.forceRender();
            // 可选：阻止默认行为（如在某些表单中，回车会触发提交）
            event.preventDefault();
        }
    });

    document.getElementById("selectedViewModalClose").addEventListener("click", () => {
        const searchInput = document.getElementById("selectedHostSearchInput");
        searchInput.value = ""
        document.getElementById("selectedViewModal").classList.remove("active")
    })

});
