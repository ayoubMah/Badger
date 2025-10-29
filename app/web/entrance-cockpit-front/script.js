// Get elements
const greeting = document.getElementById('greeting');
const names = document.getElementById('names');

// Add hover effect to greeting
greeting.addEventListener('mouseenter', () => {
    greeting.style.color = '#764ba2';
    greeting.style.transform = 'scale(1.05)';
    greeting.style.transition = 'all 0.3s ease';
});

greeting.addEventListener('mouseleave', () => {
    greeting.style.color = '#667eea';
    greeting.style.transform = 'scale(1)';
});

// Add click effect to names
names.addEventListener('click', () => {
    names.style.animation = 'bounce 0.5s ease';
    setTimeout(() => {
        names.style.animation = '';
    }, 500);
});
