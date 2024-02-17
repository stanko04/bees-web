// function filterTable() {
    $(document).ready(function(){
        $("#myInput").on("keyup", function() {
            var value = $(this).val().toLowerCase();
            $("#myTable tr").each(function() {
                var firstName = $(this).find("td:first").text().toLowerCase();
                var lastName = $(this).find("td:eq(1)").text().toLowerCase();
                if (firstName.indexOf(value) > -1 || lastName.indexOf(value) > -1) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        });
    });
// }
