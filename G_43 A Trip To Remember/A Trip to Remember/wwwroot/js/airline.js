﻿var dataTable;
$(document).ready(function () {
    loadDataTable();
})
function loadDataTable() {
    dataTable = $('#tblData').DataTable({
        "ajax": {
            "url": "/Admin/Airline/GetAll"
        },
        "columns": [
            {
                "data": "imageUrl",
                "render": function (imageUrl) {
                    return `
                        <img src="${imageUrl}" style="width: 150px; height: 100px; border: 1px solid #bbb9b9" />
                    `
                },
                "width": "15%"
            },
            { "data": "name", "width": "15%" },
            {
                "data": "id",
                "render": function (data) {
                    return `
                        <div class="w-75 btn-group" role="group">
                            <a href="/Admin/Airline/Upsert?id=${data}" class="btn btn-primary mx-2"><i class="bi bi-pencil-square"></i>Edit</a>
                            <a onClick=Delete('/Admin/Airline/Delete/${data}') class="btn btn-danger mx-2"><i class="bi bi-trash-fill"></i>Delete</a>
                        </div>
                    `
                },
                "width": "15%"
            }
        ]
    });
}
function Delete(url) {
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: url,
                type: 'DELETE',
                success: function (data) {
                    if (data.success) {
                        dataTable.ajax.reload();
                        toastr.success(data.message);
                    }
                    else {
                        toastr.error(data.message);
                    }
                }
            })
        }
    })
}