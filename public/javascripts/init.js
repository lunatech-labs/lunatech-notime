require(["jquery-1.7.1.min"], function() {
	
	$(document).ready(function() {			
		
		//  jQuery UI datapicker (lazyloaded) for date input fields
		$( ".date,input[name*='date'],input[name*='Date']" ).each(function() { 
			var $elem = $(this);
			require(["jquery-ui-1.8.18.custom.min"], function() {
				$elem.datepicker({ dateFormat: 'dd-mm-yy' }); 
			});
		});
		
	//  jQuery UI datapicker (lazyloaded) for dateswitcher
		$( "#date-picker" ).each(function() { 
			var $elem = $(this);
			require(["jquery-ui-1.8.18.custom.min"], function() {
				$elem.datepicker({ dateFormat: 'dd-mm-yy',
					onSelect: function(date) {
			            location.href = jsRoutes.controllers.HourEntries.addForDay("1", date).url;
			        }
				}); 
			});
		});
		
		//	jQuery UI autocomplete (with combobox widget) on select fields
		require(["jquery.livequery.min"], function(){
			$( "#entriesForm .entry select, #entryForm .entry select" ).livequery(function(event) {
				var $elem = $(this);
				require(["jquery-ui-1.8.18.custom.min", "combobox"], function() {
					$elem.combobox(); 
				});
			});
		});		

		//	jQuery UI autocomplete (is customized for entering tags)
		$( "input.autocomplete" ).live("focus", function(event) {	
			var $elem = $(this);
			require(["jquery-ui-1.8.18.custom.min"], function() {
				// don't navigate away from the field on tab when selecting an item
				$elem.bind( 'keydown', function(event) {
					if (event.keyCode === $.ui.keyCode.TAB && $(this).data( "autocomplete" ).menu.active) {
						event.preventDefault();
					}
				}).autocomplete({
					minLength : 0,
					source : function(request, response) {
						$.getJSON( $elem.data( "url" ), {
							term : extractLast(request.term)
						}, response);
					},
					focus : function() {
						// prevent value inserted on focus
						return false;
					},
					select : function(event, ui) {
						var terms = split(this.value);
						// remove the current input
						terms.pop();
						// add the selected item
						terms.push(ui.item.value);
						// add placeholder to get the comma-and-space at the end
						terms.push("");
						this.value = terms.join("; ");
						return false;
					}
				});
				
				function split(val) {
					return val.split(/;\s*/);
				}
				
				function extractLast(term) {
					return split(term).pop();
				}
			});	
		});
	});
});