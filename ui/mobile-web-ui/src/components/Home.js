require('normalize.css');
require('styles/App.css');

import R from 'ramda';
import React from 'react';
import Paper from 'material-ui/lib/paper';
import { Card, CardText } from 'material-ui/lib/card';
import FloatingActionButton from 'material-ui/lib/floating-action-button';
import ContentAdd from 'material-ui/lib/svg-icons/content/add';
import ContentRemove from 'material-ui/lib/svg-icons/content/remove';
import {blue500, amber500, red500} from 'material-ui/lib/styles/colors';

class HomeComponent extends React.Component {

    constructor(props) {
        super(props);
        this.step = 0.5;

        this.state = {
            currentTemp: 19.0,
            setTemp: 19.0,
            date: new Date(),
            heating: 'off',
            program: undefined,
            fromServer: {},
            error: '',
            buttonDisabled: false
        };
    }

    getTempUrl() {
        //return 'http://' + window.location.hostname + ':8080/temp';
        return 'http://192.168.1.84:8080/temp';
    }

    handleError(error) {
        let theState = this.state;
        let theError = (typeof error == 'object') ? 'Can\'t connect to the server' : error;

        this.setState(R.assoc('buttonDisabled', false, R.assoc('error', theError, theState)));
    }

    updateTemperatureReading() {
        let self = this;

        fetch(this.getTempUrl()).then(function (response) {
            if (response.status != 200) {
                throw new Error(response.statusText);
            }
            return response.json();
        }).then(function (json) {
            let currentTemp = parseFloat(json.value);
            let setTemp = parseFloat(json.setTemp);
            let heating = json.heating ? 'on' : 'off';
            let date = new Date(Date.parse(json.date));

            self.setState({
                currentTemp: currentTemp,
                setTemp: setTemp,
                fromServer: json,
                heating: heating,
                date: date,
                error: '',
                buttonDisabled: false
            });
        }).catch(function (error) {
            self.handleError(error).bind(self);
        });

    }

    componentWillUnmount() {
        this.intervals.forEach(clearInterval);
    }

    componentWillMount() {
        this.intervals = [];
        this.updateTemperatureReading();

        this.intervals.push(setInterval(this.updateTemperatureReading.bind(this), 2000));
    }

    changeTemperature(newSetTemp) {
        let self = this;
        let requestData = {
            method: 'post',
            body: '{"setTemp":' + newSetTemp + '}',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        };
        this.setState(R.assoc('buttonDisabled', true, this.state));

        fetch(this.getTempUrl(), requestData).then(function (response) {
            if (response.status != 201) {
                throw new Error(response.statusText);
            }
            self.intervals.forEach(clearInterval);
            self.intervals = [];
        }).then(function () {
            self.setState(
                R.assoc('buttonDisabled', false,
                    R.assoc('error', '',
                        R.assoc('setTemp', newSetTemp, self.state))));
            self.intervals.push(setInterval(self.updateTemperatureReading.bind(self), 2000));
        }).catch(function (error) {
            self.handleError(error).bind(self);
        });
    }

    handleUp(e) {
        e.preventDefault();

        if (this.state.setTemp > 26) return;
        let newSetTemp = this.state.setTemp + this.step;

        this.changeTemperature(newSetTemp);
    }

    handleDown(e) {
        e.preventDefault();

        if (this.state.setTemp < 1) return;
        let newSetTemp = this.state.setTemp - this.step;

        this.changeTemperature(newSetTemp);
    }


    render() {
        const marginLeft = {marginLeft: 40};
        const marginRight = {marginRight: 40};
        const errorStyle = {color: red500, textWeight: 'bold', marginTop: 20};

        let heatingStyle = {border: '4px solid ' + blue500};
        if (this.state.heating == 'on') {
            heatingStyle = {border: '4px solid ' + amber500};
        }

        return (
            <div className="makeItCenter">
                <Paper className="temperature" zDepth={5} circle={true} style={heatingStyle}>
                    <div>
                        <span>{this.state.currentTemp.toFixed(1)} °C</span>
                    </div>
                    <div className="setTo">
                        <span>Set to: {this.state.setTemp.toFixed(1)} °C</span>
                    </div>
                </Paper>
                <div style={{marginTop: 50}}>
                    <FloatingActionButton style={marginRight} onClick={this.handleDown.bind(this)}
                                          disabled={this.state.buttonDisabled}>
                        <ContentRemove />
                    </FloatingActionButton>
                    <FloatingActionButton secondary={true} style={marginLeft} onClick={this.handleUp.bind(this)}
                                          disabled={this.state.buttonDisabled}>
                        <ContentAdd />
                    </FloatingActionButton>
                </div>
                <div style={errorStyle}>{this.state.error}</div>

                <div style={{marginTop: 20}}>
                    <Card>
                        <CardText>
                            <p>Current room temperature: <strong>{this.state.currentTemp.toFixed(1)} °C</strong></p>
                            <p>Programmed temperature is: <strong>{this.state.setTemp.toFixed(1)} °C</strong></p>
                            <p>Heating is: <strong>{this.state.heating}</strong></p>
                            <p>Measured on <strong>{this.state.date.toDateString()}</strong> at&nbsp;
                                <strong>{this.state.date.toLocaleTimeString()}</strong></p>
                        </CardText>
                    </Card>
                </div>
            </div>
        );
    }
}


HomeComponent.defaultProps = {};

export default HomeComponent;
