// Test Generator Website JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize all components
    initializeNavigation();
    initializePricingModal();
    initializeAnimations();
    initializeFormValidation();
});

// Navigation functionality
function initializeNavigation() {
    // Smooth scrolling for navigation links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Navbar background change on scroll
    const navbar = document.querySelector('.navbar');
    window.addEventListener('scroll', function() {
        if (window.scrollY > 100) {
            navbar.classList.add('navbar-scrolled');
        } else {
            navbar.classList.remove('navbar-scrolled');
        }
    });
}

// Pricing modal functionality
function initializePricingModal() {
    window.showPricingModal = function(plan) {
        const modal = new bootstrap.Modal(document.getElementById('pricingModal'));
        const planSelect = document.getElementById('plan');

        if (plan === 'pro') {
            planSelect.value = 'pro';
        } else if (plan === 'enterprise') {
            planSelect.value = 'enterprise';
        }

        modal.show();
    };
}

// Animation functionality
function initializeAnimations() {
    // Intersection Observer for fade-in animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in-up');
            }
        });
    }, observerOptions);

    // Observe all feature cards and pricing cards
    document.querySelectorAll('.feature-card, .pricing-card').forEach(card => {
        observer.observe(card);
    });
}

// Form validation and submission
function initializeFormValidation() {
    const trialForm = document.getElementById('trialForm');

    if (trialForm) {
        trialForm.addEventListener('submit', function(e) {
            e.preventDefault();

            if (validateForm(this)) {
                submitTrialForm(this);
            }
        });
    }
}

function validateForm(form) {
    let isValid = true;
    const requiredFields = form.querySelectorAll('[required]');

    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        }
    });

    // Email validation
    const emailField = form.querySelector('#email');
    if (emailField && emailField.value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailField.value)) {
            emailField.classList.add('is-invalid');
            isValid = false;
        }
    }

    return isValid;
}

function submitTrialForm(form) {
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;

    // Show loading state
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';

    // Simulate form submission (replace with actual API call)
    setTimeout(() => {
        // Success state
        submitBtn.innerHTML = '<i class="fas fa-check me-2"></i>Trial Started!';
        submitBtn.classList.remove('btn-primary');
        submitBtn.classList.add('btn-success');

        // Reset form after 2 seconds
        setTimeout(() => {
            form.reset();
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
            submitBtn.classList.remove('btn-success');
            submitBtn.classList.add('btn-primary');

            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('pricingModal'));
            modal.hide();

            // Show success message
            showSuccessMessage();
        }, 2000);
    }, 1500);
}

function showSuccessMessage() {
    // Create and show success toast
    const toastHtml = `
        <div class="toast align-items-center text-white bg-success border-0 position-fixed"
             style="top: 20px; right: 20px; z-index: 9999;" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-check-circle me-2"></i>
                    Welcome to Test Generator! Your 14-day trial has started.
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto"
                        data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', toastHtml);
    const toast = new bootstrap.Toast(document.querySelector('.toast'));
    toast.show();

    // Remove toast after it's hidden
    document.querySelector('.toast').addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// Demo code highlighting (simple version)
function highlightCode() {
    // Add syntax highlighting classes to code elements
    document.querySelectorAll('code').forEach(code => {
        if (!code.classList.contains('highlighted')) {
            code.classList.add('highlighted');
            // Simple syntax highlighting could be added here
        }
    });
}

// Initialize code highlighting
highlightCode();

// Analytics tracking (placeholder for future implementation)
function trackEvent(eventName, properties) {
    // Placeholder for analytics implementation
    console.log('Event tracked:', eventName, properties);
}

// Track page views
trackEvent('page_view', { page: window.location.pathname });

// Track button clicks
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('btn')) {
        trackEvent('button_click', {
            button_text: e.target.textContent.trim(),
            button_class: e.target.className
        });
    }
});

// Error handling
window.addEventListener('error', function(e) {
    console.error('JavaScript error:', e.error);
    trackEvent('javascript_error', {
        message: e.message,
        filename: e.filename,
        lineno: e.lineno
    });
});

// Performance monitoring
window.addEventListener('load', function() {
    // Measure page load time
    const loadTime = performance.now();
    trackEvent('page_load', { load_time: loadTime });

    // Monitor performance
    if ('PerformanceObserver' in window) {
        // Monitor long tasks
        const observer = new PerformanceObserver(function(list) {
            list.getEntries().forEach(function(entry) {
                if (entry.duration > 100) { // Tasks longer than 100ms
                    trackEvent('long_task', {
                        duration: entry.duration,
                        start_time: entry.startTime
                    });
                }
            });
        });
        observer.observe({ entryTypes: ['longtask'] });
    }
});
