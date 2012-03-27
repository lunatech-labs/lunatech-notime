require(["jquery-1.7.1.min"], function() {
	
	$(document).ready(function() {
		      
		var entries = $("#weekOverview");
        renumberEntries(entries);
        
	});
	
	var renumberEntries = function(entries) {
		$( ".entry" , entries).each(function(i) {
			$( "input:not(.exclude-from-renumbering), select" , this).each(function() {
				$(this).attr( "name" , $(this).attr( "name" ).replace(/hourEntries\[.+\]/g, 'hourEntries[' + i + ']'))	
			})
		});
	}

});
