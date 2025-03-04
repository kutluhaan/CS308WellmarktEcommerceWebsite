import React, { useState } from 'react';
import { TiChevronLeftOutline, TiChevronRightOutline } from 'react-icons/ti';
import './CarouselSlider.css';

const CARDS = 7;
const MAX_VISIBILITY = 4;

const Card = ({image}) => (
    <div className='card'>
        <img src={image.url} alt={image.title} className='card-image' />
    </div>
);

const Carousel = ({children}) => {
    const [active, setActive] = useState(3);
    const count = React.Children.count(children);
    
    return (
      <div className='carousel'>
        {active > 0 && <button className='nav left' onClick={() => setActive(i => i - 1)}><TiChevronLeftOutline/></button>}
        {React.Children.map(children, (child, i) => (
          <div className='card-container' style={{
              '--active': i === active ? 1 : 0,
              '--offset': (active - i) / 3,
              '--direction': Math.sign(active - i),
              '--abs-offset': Math.abs(active - i) / 3,
              'pointerEvents': active === i ? 'auto' : 'none',
              'opacity': Math.abs(active - i) >= MAX_VISIBILITY ? '0' : '1',
              'display': Math.abs(active - i) > MAX_VISIBILITY ? 'none' : 'block',
            }}>
            {child}
          </div>
        ))}
        {active < count - 1 && <button className='nav right' onClick={() => setActive(i => i + 1)}><TiChevronRightOutline/></button>}
      </div>
    );
};

const CarouselSlider = () => {
    const slides = [
        {url: '/assets/coffee-1.png', title: 'coffee-1'},
        {url: '/assets/coffee-2.jpg', title: 'coffee-2'},
        {url: '/assets/coffee-3.png', title: 'coffee-3'},
        {url: '/assets/scentedcandle-1.png', title: 'scentedcandle-1'},
        {url: '/assets/scentedcandle-2.png', title: 'scentedcandle-2'},
        {url: '/assets/vinly-record-player-1.jpg', title: 'vinly-record-player-1'},
        {url: '/assets/vinly-record-player-2.png', title: 'vinly-record-player-2'},
    ];
    return (
      <div className='carousel-slider'>
        <Carousel>
          {[...new Array(CARDS)].map((_, i) => (
            <Card key={i} image={slides[i % slides.length]}/>
          ))}
        </Carousel>
      </div>
    );
};

export default CarouselSlider;
