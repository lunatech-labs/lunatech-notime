require(["jquery-1.7.1.min"], function() {
	
	$(document).ready(function() {			
		
		//  jQuery UI datapicker (lazyloaded) for date input fields
		$( ".date,input[name*='date'],input[name*='Date']" ).live('click', function() {
			var $elem = $(this)
			require(["jquery-ui-1.8.18.custom.min"], function() {
				$elem.datepicker({
					firstDay: 1,
					dateFormat: 'dd-mm-yy',
					showWeek: true
				}).focus()
			})
		})

		// jQuery UI datapicker (lazyloaded) for date range fields
		if($( "#beginDate, #endDate" ).length) {
			require(["jquery-ui-1.8.18.custom.min"], function() {
				var dates = $( "#beginDate, #endDate" ).datepicker({
					firstDay: 1,
					dateFormat: 'dd-mm-yy',
					showWeek: true,
					onSelect: function( selectedDate ) {
					var option = this.id == "beginDate" ? "minDate" : "maxDate",
						instance = $( this ).data( "datepicker" ),
						date = $.datepicker.parseDate(
							instance.settings.dateFormat ||
							$.datepicker._defaults.dateFormat,
							selectedDate, instance.settings )
						dates.not( this ).datepicker( "option", option, date )
					}
				})
			})
		}
		
		//  jQuery UI datapicker (lazyloaded) for dateswitcher in menu
		$( "#date-picker" ).each(function() { 
			var $elem = $(this)
			require(["jquery-ui-1.8.18.custom.min"], function() {
				$elem.datepicker({ firstDay: 1, dateFormat: 'dd-mm-yy', showWeek: true,
					onSelect: function(date) {
			            location.href = jsRoutes.controllers.HourEntries.addForDay(date).url
			        }
				})
			})
		})
		
		//	jQuery UI autocomplete (with combobox widget) on select fields
		if ($( "#entriesForm .entry select, #entryForm .entry select, #assignments" ).length) {
			$( "#entriesForm .entry select, #entryForm .entry select, #assignments" ).each(function() {
				var $elem = $(this)
				require(["jquery-ui-1.8.18.custom.min", "combobox"], function() {
					$elem.combobox()
				})
			})
		}

		// jQuery UI autocomplete (with combobox widget) on select fields which are added by jQuery
		if ($( "#entriesForm .entry" ).length) {
			require(["jquery.livequery.min"], function(){
				$( "#entriesForm .entry select" ).livequery(function() {
					var $elem = $(this)
					require(["jquery-ui-1.8.18.custom.min", "combobox"], function() {
						$elem.combobox()
					})
				})
			})
		}

		//	jQuery UI autocomplete (is customized for entering tags)
		$( "input.autocomplete" ).live("focus", function(event) {	
			var $elem = $(this)
			require(["jquery-ui-1.8.18.custom.min"], function() {
				// don't navigate away from the field on tab when selecting an item
				$elem.bind( 'keydown', function(event) {
					if (event.keyCode === $.ui.keyCode.TAB && $(this).data( "autocomplete" ).menu.active) {
						event.preventDefault()
					}
				}).autocomplete({
					minLength : 0,
					source : function(request, response) {
						$.getJSON( $elem.data( "url" ), {
							term : extractLast(request.term)
						}, response)
					},
					focus : function() {
						// prevent value inserted on focus
						return false
					},
					select : function(event, ui) {
						var terms = split(this.value)
						// remove the current input
						terms.pop()
						// add the selected item
						terms.push(ui.item.value)
						// add placeholder to get the comma-and-space at the end
						terms.push("")
						this.value = terms.join("; ")
						return false
					}
				})
				
				function split(val) {
					return val.split(/;\s*/)
				}
				
				function extractLast(term) {
					return split(term).pop()
				}
			})
		})
		
	})
})