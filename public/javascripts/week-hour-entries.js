require(["jquery-1.7.1.min"], function() {
	
	$(document).ready(function() {
        renumberEntries($( '#weekOverview' ))
        calculateTotals()
	})

	$( 'input[name*="hours"], input[name*="minutes"]' ).click(function() {
		var entryTagsStringField = $( 'input.tagsString', $(this).parents( '.entry' ) )
		var entryTagsString = entryTagsStringField.val()
		$( '#tagsString' ).val(entryTagsString)
		$( '#tagsString' ).data('name', entryTagsStringField.attr('name'))
	})

	$( '#tagsString' ).focusout(function() {
		var tagsString = $(this).val()
		var entryName = $( '#tagsString' ).data('name')
		var entryTagsStringField = $( 'input[name="' + entryName + '"]' )
		entryTagsStringField.val(tagsString)
		$(this).val("")
	})

	$( 'input[name*="hours"], input[name*="minutes"]' ).keyup(function() {
		calculateTotals()
	})

	$( 'input[name*="hours"], input[name*="minutes"], #tagsString' ).change(function() {
		$( '#hours-not-saved-warning' ).show()
	})

	$('#entriesForm').submit(function() {
	    $('tr.template').remove()
	})

	$( '.addAssignment' ).live('click', function() {
		var assignmentId = $( '#assignments' ).val()
		var projectName = $( '#assignments option:selected' ).text()

		if (assignmentId) {
		    var assignmentRows = $( '#weekOverview > tbody' )
		    var template = $( '.template', assignmentRows )
		    var assignment = template.clone()
		    template.before(assignment)

		    assignment.removeClass('template hidden')
		    $( '.projectName', assignment ).html(projectName)
		    $( '.assignmentId', assignment ).each(function() {
		    	$(this).val(assignmentId)
		    })

		    renumberEntries($( '#weekOverview' ))
		}
	})

	$( '.addEntry' ).live('click', function() {
		var day = $(this).parents( '.day' )
		var entryTemplate = $( '.entry:last', day )
		var entry = entryTemplate.clone()
		entryTemplate.after(entry)

		$( '.hours', entry ).val("")
		$( '.minutes', entry ).val("")

		renumberEntries($( '#weekOverview' ))
	})
	
	var renumberEntries = function(entries) {
		$( 'tr:not(.hidden) td .entry' , entries).each(function(i) {
			$( 'input:not(.exclude-from-renumbering), select' , this).each(function() {
				$(this).attr( 'name' , $(this).attr( 'name' ).replace(/hourEntries\[.+\]/g, 'hourEntries[' + i + ']'))
			})
		})
	}

	var calculateTotals = function() {
		calculateDayTotals()
		calculateProjectTotals()
		calculateTotalTotals()
	}

	var calculateDayTotals = function() {
		$( "tbody tr.assignment:first td.day" ).each(function(i) {
			var totalHours = 0
			var totalMinutes = 0
			$( "tbody tr.assignment" ).each(function() {
				$( "td.day:eq(" + i + ")", $(this) ).each(function() {
					$( ".entry", $(this) ).each(function() {
    					$( 'input[name*="hours"]', $(this) ).each(function() {
    						var hours = parseInt($(this).val())
    						totalHours += !isNaN(hours) ? hours : 0
        				})
        				$( 'input[name*="minutes"]', $(this) ).each(function() {
    						var minutes = parseInt($(this).val())
    						totalMinutes += !isNaN(minutes) ? minutes : 0
        				})
    				})
        		})
			})
			$( "tfoot tr td.dayTotal:eq(" + i + ")").html(formatTotalHoursMinutes(totalHours, totalMinutes));
		})
	}

	var calculateProjectTotals = function() {
		$( "tbody tr.assignment" ).each(function() {
			var totalHours = 0
			var totalMinutes = 0
			$( "td.day", $(this) ).each(function() {
				$( ".entry", $(this) ).each(function() {
					$( 'input[name*="hours"]', $(this) ).each(function() {
						var hours = parseInt($(this).val())
						totalHours += !isNaN(hours) ? hours : 0
    				})
    				$( 'input[name*="minutes"]', $(this) ).each(function() {
						var minutes = parseInt($(this).val())
						totalMinutes += !isNaN(minutes) ? minutes : 0
    				})
				})
			})
			$( "td.total", $(this) ).html(formatTotalHoursMinutes(totalHours, totalMinutes))
		})
	}

	var calculateTotalTotals = function() {
    	var totalHours = 0
    	var totalMinutes = 0
    	$( "tbody tr td.total" ).each(function(i) {
    		var hoursMinutes = $(this).text()
    		var split = hoursMinutes.match(/\d+/g)
    		totalHours += parseInt(split[0]);
    		totalMinutes += parseInt(split[1]);
   		})
		$( "tfoot tr td.total ").html(formatTotalHoursMinutes(totalHours, totalMinutes))
	}

	var formatTotalHoursMinutes = function(hours, minutes) {
		hours += parseInt(minutes / 60)
		minutes = parseInt(minutes % 60)
		return hours + "h " + minutes + "m"
	}

});
