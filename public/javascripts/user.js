require(["jquery-1.7.1.min"], function() {

	$(document).ready(function() {
		
		$('.removeRole').live('click', function(e) {
			var roles = $(this).parents('.roles')
	        $(this).parents('.role').remove()
	        renumber(roles)
	    })

	    $('.addRole').live('click', function(e) {
	        var roles = $(this).parents('.roles')
	        var template = $('.role-template', roles)
	        template.before('<div class="clearfix role">' + template.html() + '</div>')
	        renumber(roles)
	    })
	    
	    $('#user-form').submit(function() {
	        $('.role-template').remove()
	    })
		
	    var renumber = function(roles) {
	        $('.role select', roles).each(function(i) {
	        	$(this).attr('name', $(this).attr('name').replace(/userRoles\[.+\]/g, 'userRoles[' + i + ']'))
	    	})
	    }
	})
	
})