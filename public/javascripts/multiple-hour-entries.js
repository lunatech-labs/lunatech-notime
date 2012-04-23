require(["jquery-1.7.1.min"], function() {
	
	$(document).ready(function() {
		
		//	jQuery for createHourEntries.scala.html
        $( ".removeEntry" ).live('click', function(e) {
            var entries = $(this).parents('.entries')
            $(this).parents( ".entry" ).remove()
            renumberEntries(entries)
        });
        
        $( ".addEntry" ).live('click', function(e) {
            var entries = $(this).parents('.entries')
            var template = $( ".entry_template" , entries)
            template.before( '<div class="entry">' + template.html() + '</div>' )
            renumberEntries(entries)
        });  
        
        $( "#entriesForm" ).submit(function() {
            $( ".entry_template" ).remove()
        });
        
        var renumberEntries = function(entries) {
			$( ".entry" , entries).each(function(i) {
				$( "input:not(.exclude-from-renumbering), select" , this).each(function() {
					$(this).attr( "name" , $(this).attr( "name" ).replace(/hourEntryForms\[.+\]/g, 'hourEntryForms[' + i + ']'))
				})
			});
		}
        
	});

});
