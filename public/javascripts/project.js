require(["jquery-1.7.1.min"], function() {

	$(document).ready(function() {
		
		$('.removeTag').live('click', function(e) {
			var tags = $(this).parents('.tags')
	        $(this).parents('.tag').remove()
	        renumber(tags)
	    })

	    $('.addTag').live('click', function(e) {
	        var tags = $(this).parents('.tags')
	        var template = $('.tag-template', tags)
	        template.before('<div class="clearfix tag">' + template.html() + '</div>')
	        renumber(tags)
	    })
	    
	    $('#project-form').submit(function() {
	        $('.tag-template').remove()
	    })
		
	    var renumber = function(tags) {
	        $('.tag select', tags).each(function(i) {
	        	$(this).attr('name', $(this).attr('name').replace(/requiredTags\[.+\]/g, 'requiredTags[' + i + ']'))
	    	})
	    }
	})
	
})