var ajaxUrl = "ajax/meals/";
var datatableApi;

// $(document).ready(function () {
$(function () {
    datatableApi = $("#datatable").DataTable({
        "paging": true,
        "info": true,
        "columns": [
            {
                "data": "description"
            },
            {
                "data": "calories"
            },
            {
                "data": "dateTime"
            },
            {
                "defaultContent": "Delete",
                "orderable": false
            }
        ],
        "order": [
            [
                0,
                "asc"
            ]
        ]
    });
    makeEditable();
});

function filter() {
    var form = $("#filter");
    $.ajax({
        url: ajaxUrl + "filter",
        type: form.attr('method'), //send it through get method
        data: form.serialize(),
        success: function(data) {
            datatableApi.clear().rows.add(data).draw();
        }
    });
}

function dropFilter() {
    $.ajax({
        url: ajaxUrl,
        type: "GET", //send it through get method
        success: function(data) {
            datatableApi.clear().rows.add(data).draw();
        }
    });
}
