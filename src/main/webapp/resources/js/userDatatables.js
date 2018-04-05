var ajaxUrl = "ajax/admin/users/";
var datatableApi;

// $(document).ready(function () {
$(function () {
    datatableApi = $("#datatable").DataTable({
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "email"
            },
            {
                "data": "roles"
            },
            {
                "data": "enabled"
            },
            {
                "data": "registered"
            },
            {
                "defaultContent": "Edit",
                "orderable": false
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

    $(".userCheckbox").change(function() {
//-->        alert($(this).attr('id') + " " + this.checked);  //-->this will alert id of checked checkbox.
        $.ajax({
            type: "POST",
            url: ajaxUrl + "check",
            data: {
                id: $(this).attr('id'),
                check: this.checked
            },
            success: function () {
                updateTable();
                successNoty("Edited");
            }
        });
    });

    makeEditable();
});