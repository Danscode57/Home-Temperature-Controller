require('normalize.css');
require('styles/App.css');

import R from 'ramda';
import React from 'react';
import AppBar from 'material-ui/lib/app-bar';
import LeftNav from 'material-ui/lib/left-nav';
import MenuItem from 'material-ui/lib/menus/menu-item';

class Navigation extends React.Component {
    constructor(props) {
        super(props);

        this.state = {open: false, title: 'Home Temp', selected: 'home'};
    }

    openLeftNav() {
        this.setState(R.assoc('open', true)(this.state));
    }

    closeLeftNav() {
        this.setState(R.assoc('open', false)(this.state));
    }

    openSchedule() {
        this.setState(R.assoc('selected', 'schedule')(this.state));
        this.closeLeftNav();
        this.context.router.push('schedule');
    }

    openHome() {
        this.setState(R.assoc('selected', 'home')(this.state));
        this.closeLeftNav();
        this.context.router.push('home');
    }

    render() {
        return (
            <div>
                <AppBar title="Home Temp"
                        onLeftIconButtonTouchTap={this.openLeftNav.bind(this)}/>
                <LeftNav open={this.state.open} docked={false}
                         onRequestChange={this.closeLeftNav.bind(this)}>
                    <MenuItem onTouchTap={this.openHome.bind(this)}>Home</MenuItem>
                    <MenuItem onTouchTap={this.openSchedule.bind(this)}>Schedule</MenuItem>
                </LeftNav>
            </div>
        );
    }
}

Navigation.contextTypes = {
    router: React.PropTypes.object
};

export default Navigation;