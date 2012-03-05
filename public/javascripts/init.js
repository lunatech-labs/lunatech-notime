/*! Front-end Patterns Init script 
    
    Copyright (c) 2009-2012 Lunatech Research, Egor Kloos
    
    */

    // INIT        
	$(document).ready(function() {
	
		//  jQuery UI datapicker (lazyloaded)
		$(".date,input[name*='date'],input[name*='Date']").each(function() {
			var $elem = $(this);
			$elem.datepicker({
				dateFormat : 'dd-mm-yy'
			});
		});
	
	});    
    