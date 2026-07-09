/**
 * ============================================================
 * PARTICLES.JS - Animated Water Droplet Background
 * Creates floating water particles for KKK Kar Wash website
 * ============================================================
 */

(function() {
    'use strict';

    const CONFIG = {
        particleCount: 120,
        minSize: 1,
        maxSize: 4,
        minSpeed: 0.2,
        maxSpeed: 0.8,
        minOpacity: 0.05,
        maxOpacity: 0.25,
        wobbleAmount: 0.3,
        color: '56, 189, 248',
    };

    const canvas = document.getElementById('particles-canvas');
    if (!canvas) {
        console.warn('Particles canvas not found.');
        return;
    }

    const ctx = canvas.getContext('2d');
    let width, height;
    let particles = [];
    let animationId = null;
    let isVisible = true;

    function resizeCanvas() {
        width = canvas.width = window.innerWidth;
        height = canvas.height = window.innerHeight;
        particles.forEach(p => {
            p.x = Math.random() * width;
            if (p.y > height) p.y = Math.random() * height - height;
        });
    }

    class Particle {
        constructor() { this.reset(); }
        reset() {
            this.x = Math.random() * width;
            this.y = Math.random() * height - height;
            this.size = Math.random() * (CONFIG.maxSize - CONFIG.minSize) + CONFIG.minSize;
            this.speed = Math.random() * (CONFIG.maxSpeed - CONFIG.minSpeed) + CONFIG.minSpeed;
            this.opacity = Math.random() * (CONFIG.maxOpacity - CONFIG.minOpacity) + CONFIG.minOpacity;
            this.wobbleOffset = Math.random() * Math.PI * 2;
            this.wobbleSpeed = Math.random() * 0.02 + 0.01;
            this.rotation = Math.random() * 360;
            this.rotationSpeed = (Math.random() - 0.5) * 0.5;
        }
        update() {
            this.y += this.speed;
            this.x += Math.sin(this.y * this.wobbleSpeed + this.wobbleOffset) * CONFIG.wobbleAmount;
            this.rotation += this.rotationSpeed;
            if (this.y > height + 50) {
                this.reset();
                this.y = -10 - Math.random() * 20;
                this.x = Math.random() * width;
            }
            if (this.x < -50) this.x = width + 50;
            if (this.x > width + 50) this.x = -50;
        }
        draw() {
            ctx.save();
            ctx.translate(this.x, this.y);
            ctx.rotate(this.rotation * Math.PI / 180);

            const w = this.size * 0.8;
            const h = this.size * 1.2;

            ctx.beginPath();
            ctx.moveTo(0, -h * 0.6);
            ctx.bezierCurveTo(-w * 0.8, -h * 0.2, -w * 0.6, h * 0.2, 0, h * 0.5);
            ctx.bezierCurveTo(w * 0.6, h * 0.2, w * 0.8, -h * 0.2, 0, -h * 0.6);
            ctx.closePath();

            const gradient = ctx.createRadialGradient(
                -this.size * 0.2, -this.size * 0.2, this.size * 0.1,
                0, 0, this.size * 0.8
            );
            gradient.addColorStop(0, `rgba(255, 255, 255, ${this.opacity * 0.6})`);
            gradient.addColorStop(0.3, `rgba(${CONFIG.color}, ${this.opacity})`);
            gradient.addColorStop(1, `rgba(${CONFIG.color}, ${this.opacity * 0.3})`);

            ctx.fillStyle = gradient;
            ctx.fill();

            ctx.beginPath();
            ctx.arc(-this.size * 0.25, -this.size * 0.25, this.size * 0.2, 0, Math.PI * 2);
            ctx.fillStyle = `rgba(255, 255, 255, ${this.opacity * 0.4})`;
            ctx.fill();

            ctx.restore();
        }
    }

    function initParticles() {
        particles = [];
        for (let i = 0; i < CONFIG.particleCount; i++) {
            const p = new Particle();
            p.y = Math.random() * height;
            particles.push(p);
        }
    }

    function animate() {
        if (!isVisible) {
            animationId = requestAnimationFrame(animate);
            return;
        }
        ctx.clearRect(0, 0, width, height);
        particles.forEach(p => { p.update(); p.draw(); });
        animationId = requestAnimationFrame(animate);
    }

    function startAnimation() {
        if (animationId) cancelAnimationFrame(animationId);
        isVisible = true;
        animate();
    }

    function stopAnimation() {
        isVisible = false;
        if (animationId) {
            cancelAnimationFrame(animationId);
            animationId = null;
        }
    }

    function handleVisibilityChange() {
        if (document.hidden) stopAnimation();
        else startAnimation();
    }

    function detectPerformance() {
        const isMobile = /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);
        const isLowEnd = navigator.deviceMemory && navigator.deviceMemory < 4;
        if (isMobile || isLowEnd) {
            CONFIG.particleCount = 60;
            CONFIG.maxOpacity = 0.15;
        }
    }

    function init() {
        detectPerformance();
        resizeCanvas();
        initParticles();
        startAnimation();
        window.addEventListener('resize', resizeCanvas);
        document.addEventListener('visibilitychange', handleVisibilityChange);
        window.addEventListener('beforeunload', function() {
            stopAnimation();
            window.removeEventListener('resize', resizeCanvas);
            document.removeEventListener('visibilitychange', handleVisibilityChange);
        });
        console.log(`Particles: ${CONFIG.particleCount} particles initialized`);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();