# Test Generator Website

This is the official marketing website for Test Generator - an automated JUnit test generation tool.

## 🚀 Quick Start

### Option 1: GitHub Pages (Free)
1. Fork this repository
2. Go to Settings → Pages
3. Select "Deploy from a branch"
4. Choose `main` branch and `/website` folder
5. Your site will be live at `https://yourusername.github.io/test-generator/`

### Option 2: Netlify (Free)
1. Connect your GitHub repository to Netlify
2. Set build command: `echo "No build required"`
3. Set publish directory: `website`
4. Deploy!

### Option 3: Vercel (Free)
1. Import your GitHub repository
2. Vercel will auto-detect it's a static site
3. Set root directory to `website`
4. Deploy!

## 📁 Project Structure

```
website/
├── index.html          # Main landing page
├── docs.html           # Documentation page
├── css/
│   └── style.css       # Custom styles
├── js/
│   └── main.js         # JavaScript functionality
└── images/             # Images and assets (add as needed)
```

## 🎨 Customization

### Colors and Branding
Edit `css/style.css` to customize:
- Primary colors: `--primary-color`
- Gradients: `--gradient-primary`, `--gradient-secondary`
- Typography and spacing

### Content
Edit the HTML files to update:
- Pricing information
- Feature descriptions
- Contact information
- Demo code examples

### Forms
The contact/trial forms currently log to console. To make them functional:
1. Add your backend API endpoints
2. Implement proper form submission
3. Add email service integration

## 📊 Analytics

To add analytics, edit `js/main.js` and add your tracking codes:
- Google Analytics
- Mixpanel
- Plausible
- Or any other analytics service

## 🔧 Development

### Local Development
```bash
# Serve locally (requires Python)
cd website
python -m http.server 8000

# Or use any static server
npx serve website
```

### Testing Forms
The trial signup form currently shows a success message. To integrate with a real service:
1. Replace the `submitTrialForm()` function
2. Add your CRM or email service API
3. Implement proper error handling

## 📈 Marketing Features

### Built-in Features
- ✅ Responsive design (mobile-first)
- ✅ Fast loading (no heavy frameworks)
- ✅ SEO optimized
- ✅ Accessibility compliant
- ✅ Modern UI with animations
- ✅ Pricing calculator
- ✅ Demo code highlighting
- ✅ Contact forms
- ✅ Newsletter signup

### Conversion Optimization
- Clear value proposition
- Social proof elements
- Risk-free trial offers
- Multiple CTAs
- Benefit-focused copy
- Professional testimonials section (add when available)

## 🚀 Deployment Checklist

- [ ] Update pricing information
- [ ] Add real contact information
- [ ] Set up analytics tracking
- [ ] Configure form submissions
- [ ] Add company logo
- [ ] Include customer testimonials
- [ ] Set up monitoring/alerts
- [ ] Test all links and forms
- [ ] Optimize images
- [ ] Check mobile responsiveness

## 📞 Support

For website customization or deployment help:
- Email: support@testgenerator.com
- GitHub Issues: Report bugs or request features

## 📄 License

This website is part of the Test Generator project. See main project LICENSE for details.
