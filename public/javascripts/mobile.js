$(function() {
	$("#teamButton").live('click', function() {
		$("#teamButton").html("Cancel");
		$("#addPlayerButton").toggle();
		$("#buildTeamButton").toggle();
		$("#home li small").toggle();
		$("#home li").toggleClass("arrow");
	});
	
	$("#addPlayerButton").live('click', function() {
		$("#add input").val("");
		$("#add input[type=checkbox]").attr("checked", false);
	});
});