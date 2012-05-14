require(["jquery-1.7.1.min"], function() {
	
	$(document).ready(function() {
		      
		var entries = $("#weekOverview")
        renumberEntries(entries)
        
        calculateDayTotals()
		calculateProjectTotals()
		calculateTotalTotals()

	})

	$( 'input[name*="hours"], input[name*="minutes"]' ).keyup(function() {
		calculateDayTotals()
		calculateProjectTotals()
		calculateTotalTotals()
	})

	$( 'input[name*="hours"], input[name*="minutes"]' ).change(function() {
		$("#hours-not-saved-warning").show()
	})
	
	var renumberEntries = function(entries) {
		$( ".entry" , entries).each(function(i) {
			$( "input:not(.exclude-from-renumbering), select" , this).each(function() {
				$(this).attr( "name" , $(this).attr( "name" ).replace(/hourEntries\[.+\]/g, 'hourEntries[' + i + ']'))	
			})
		})
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
