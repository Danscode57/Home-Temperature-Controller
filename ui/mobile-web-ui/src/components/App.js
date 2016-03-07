require('normalize.css');
require('styles/App.css');

import React from 'react';
import Navigation from './Navigation';

class AppComponent extends React.Component {
    render() {
        return (
            <div>
                <Navigation />
                {this.props.children}
            </div>
        );
    }
}

export default AppComponent;