import React from 'react';
import { Container, Typography, Box, Paper } from '@mui/material';

const AboutPage = () => {
  return (
    <Container className='container-wrapper' maxWidth="md">
      <Paper elevation={3} style={{ padding: '2rem' }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          About Us
        </Typography>
        <Box marginBottom="1.5rem">
          <Typography variant="body1">
            Welcome to Wellmarkt, your trusted destination for well-being products. 
            We are dedicated to providing high-quality coffee, personal care items, and 
            other products that enhance your lifestyle. 
          </Typography>
        </Box>
        <Box marginBottom="1.5rem">
          <Typography variant="h6" gutterBottom>
            Our Mission
          </Typography>
          <Typography variant="body1">
            At Wellmarkt, our mission is to empower individuals to lead healthier, 
            more fulfilling lives through our thoughtfully curated product offerings.
          </Typography>
        </Box>
        <Box marginBottom="1.5rem">
          <Typography variant="h6" gutterBottom>
            Contact Us
          </Typography>
          <Typography variant="body1">
            Have questions or feedback? We'd love to hear from you! Reach us at: 
            <br />
            <strong>Email:</strong> support@wellmarkt.com
            <br />
            <strong>Phone:</strong> +123 456 7890
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
};

export default AboutPage;
