/**
 * Mobile Navigation Handler
 * Gerencia o menu hamburguê em dispositivos móveis
 */

document.addEventListener('DOMContentLoaded', function() {
    const navToggle = document.querySelector('.navbar__toggle');
    const navMenu = document.querySelector('.navbar__menu');
    const navLinks = document.querySelectorAll('.navbar__link, .navbar__button');

    // Toggle menu when hamburger clicked
    if (navToggle) {
        navToggle.addEventListener('click', function(e) {
            e.preventDefault();
            toggleMenu();
        });
    }

    // Close menu when a link is clicked
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            closeMenu();
        });
    });

    // Close menu when clicking outside
    document.addEventListener('click', function(e) {
        if (navMenu && navToggle && !navMenu.contains(e.target) && !navToggle.contains(e.target)) {
            closeMenu();
        }
    });

    // Close menu on window resize
    window.addEventListener('resize', function() {
        if (window.innerWidth >= 768) {
            closeMenu();
        }
    });

    function toggleMenu() {
        if (navToggle && navMenu) {
            navToggle.classList.toggle('active');
            navMenu.classList.toggle('active');
            
            // Prevent body scroll when menu is open
            if (navMenu.classList.contains('active')) {
                document.body.style.overflow = 'hidden';
            } else {
                document.body.style.overflow = '';
            }
        }
    }

    function closeMenu() {
        if (navToggle && navMenu) {
            navToggle.classList.remove('active');
            navMenu.classList.remove('active');
            document.body.style.overflow = '';
        }
    }

    // Handle touch outside menu to close
    if (document.addEventListener) {
        document.addEventListener('touchstart', function(e) {
            if (navMenu && navMenu.classList.contains('active')) {
                if (!navMenu.contains(e.target) && !navToggle.contains(e.target)) {
                    closeMenu();
                }
            }
        }, false);
    }
});

/**
 * Smooth scrolling for hash links
 */
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        const href = this.getAttribute('href');
        
        if (href !== '#' && document.querySelector(href)) {
            e.preventDefault();
            
            const element = document.querySelector(href);
            element.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

/**
 * Utility to detect and handle mobile devices
 */
const isMobile = {
    Android: function() {
        return /Android/i.test(navigator.userAgent);
    },
    BlackBerry: function() {
        return /BlackBerry/i.test(navigator.userAgent);
    },
    iOS: function() {
        return /iPhone|iPad|iPod/i.test(navigator.userAgent);
    },
    Opera: function() {
        return /Opera Mini/i.test(navigator.userAgent);
    },
    Windows: function() {
        return /IEMobile|WPDesktop/i.test(navigator.userAgent);
    },
    any: function() {
        return (this.Android() || this.BlackBerry() || this.iOS() || this.Opera() || this.Windows());
    }
};

// Add mobile class to body if on mobile device
if (isMobile.any()) {
    document.body.classList.add('is-mobile');
}
