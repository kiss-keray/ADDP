import * as React from 'react';
import * as ReactDOM from 'react-dom';
import App from './App';
import './index.css';
import {createStore} from 'redux'
import { Provider } from 'react-redux';
import rootReducer from './rpc-redux/reducer';
// import { connect } from 'react-redux';
window['AddpContext'] = {};
const store = createStore(rootReducer, (window as any).__REDUX_DEVTOOLS_EXTENSION__ && (window as any).__REDUX_DEVTOOLS_EXTENSION__());
// store.subscribe(() => console.log(store.getState()))
// const APP = connect(dispatch => ({dispatch}))(App);
ReactDOM.render(
  <Provider store={store}>
    <App/>
  </Provider>,
  document.getElementById('root') as HTMLElement
);