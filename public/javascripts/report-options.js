require(["jquery-1.7.1.min"], function() {

	$(document).ready(function() {
		
		$('.removeCustomer').live('click', function(e) {
			var customers = $(this).parents('.customers')
	        $(this).parents('.customer').remove()
	        renumberCustomers(customers)
	    })

	    $('.addCustomer').live('click', function(e) {
	        var customers = $(this).parents('.customers')
	        var template = $('.customer-template', customers)
	        template.before('<div class="clearfix customer">' + template.html() + '</div>')
	        renumberCustomers(customers)
	    })
	    
	    $('.removeProject').live('click', function(e) {
	    	var projects = $(this).parents('.projects')
	        $(this).parents('.project').remove()
	        renumberProjects(projects)
	    })

	    $('.addProject').live('click', function(e) {
	        var projects = $(this).parents('.projects')
	        var template = $('.project-template', projects)
	        template.before('<div class="clearfix project">' + template.html() + '</div>')
	        renumberProjects(projects)
	    })

	    $('.removeUser').live('click', function(e) {
	    	var users = $(this).parents('.users')
	        $(this).parents('.user').remove()
	        renumberUsers(users)
	    })

	    $('.addUser').live('click', function(e) {
	        var users = $(this).parents('.users')
	        var template = $('.user-template', users)
	        template.before('<div class="clearfix user">' + template.html() + '</div>')
	        renumberUsers(users)
	    })

	    $('#options-form').submit(function() {
	        $('.customer-template').remove()
	        $('.project-template').remove()
	        $('.user-template').remove()
	    })

	    var renumberCustomers = function(customers) {
	        $('.customer select', customers).each(function(i) {
	        	$(this).attr('name', $(this).attr('name').replace(/customers\[.+\]/g, 'customers[' + i + ']'))
	    	})
	    }
		
		var renumberProjects = function(projects) {
	        $('.project select', projects).each(function(i) {
	        	$(this).attr('name', $(this).attr('name').replace(/projects\[.+\]/g, 'projects[' + i + ']'))
	    	})
	    }
		
		var renumberUsers = function(users) {
	        $('.user select', users).each(function(i) {
	        	$(this).attr('name', $(this).attr('name').replace(/users\[.+\]/g, 'users[' + i + ']'))
	    	})
	    }

	});

});