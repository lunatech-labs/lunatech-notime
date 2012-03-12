$(function() {
	function split(val) {
		return val.split(/;\s*/);
	}
	function extractLast(term) {
		return split(term).pop();
	}

	$('input.autocomplete').live( 'focus', function(event) {	
		var $input = $(this);
		// don't navigate away from the field on tab when selecting an item
		$input.bind( 'keydown', function(event) {
			if (event.keyCode === $.ui.keyCode.TAB && $(this).data("autocomplete").menu.active) {
				event.preventDefault();
			}
		}).autocomplete({
			minLength : 0,
			source : function(request, response) {
				$.getJSON( $input.data('url'), {
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
	});
});