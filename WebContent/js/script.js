$( document ).ready(function() {
    

    //Slick Carousel Reel
	$('.slick-reel').slick({
	  centerMode: true,
	  centerPadding: '15px',
	  slidesToShow: 5,
	  autoplay: false,
  	  autoplaySpeed: 5000,
  	  infinite: true,
	  responsive: [
	    {
	      breakpoint: 768,
	      settings: {
	        arrows: false,
	        centerMode: true,
	        centerPadding: '40px',
	        slidesToShow: 3
	      }
	    },
	    {
	      breakpoint: 480,
	      settings: {
	        arrows: false,
	        centerMode: true,
	        centerPadding: '40px',
	        slidesToShow: 1
	      }
	    }
	  ]
	});

	//Slick Carousel Line
	$('.slick-line').slick({
	  infinite: true,
	  slidesToShow: 6,
	  autoplay: false,
	  arrows: false,
	  responsive: [
	    {
	      breakpoint: 768,
	      settings: {
	        arrows: false,
	        centerMode: true,
	        centerPadding: '40px',
	        slidesToShow: 3
	      }
	    },
	    {
	      breakpoint: 480,
	      settings: {
	        arrows: false,
	        centerMode: true,
	        centerPadding: '40px',
	        slidesToShow: 1
	      }
	    }
	  ]
	});

	$('.slick-line .card').click(
		function(e){
			e.preventDefault();
			var infoExport = $(this).find('div.info').html();
			$(".card-showcase").slideUp();
			$(this).closest(".slick-line-container").find(".card-showcase").html("<div class='info'>"+infoExport+"</div>");
			$(this).closest(".slick-line-container").find(".card-showcase").slideToggle();
		}
	);
});