require(["jquery-1.7.1.min"], function() {
	
	$(document).ready(function() {	
		
		//	jQuery for createCustomer.scala.html and editCustomer.scale.html
        $( ".removeManager" ).live('click', function(e) {
            var manager = $(this).parents( ".managers" )
            $(this).parents( ".manager" ).remove()
            renumberManagers(manager)
        })
        
        $( ".addManager" ).live('click', function(e) {
            var manager = $(this).parents( ".managers" )
            var template = $( ".manager_template" , manager)
            template.before('<div class="clearfix manager">' + template.html() + '</div>')
            renumberManagers(manager)
        })
        
        $( "#form" ).submit(function() {
            $( ".manager_template" ).remove()
        })
        
        var renumberManagers = function(manager) {
			$( ".manager select" , manager).each(function(i) {
				$(this).attr( "name" , $(this).attr( "name" ).replace(/customerManagers\[.+\]/g, 'customerManagers[' + i + ']'))
			})
        }
        
	});
	
});