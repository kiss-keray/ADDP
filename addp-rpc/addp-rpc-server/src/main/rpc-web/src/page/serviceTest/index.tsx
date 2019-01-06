import * as React from 'react';
import { withRouter } from 'react-router-dom';
import { connect, ConnectedComponentClass } from 'react-redux'
import { setNavType } from '../../rpc-redux/actions'
import Fetch from '../../Fetch';
import { message, Button } from 'antd';
class ServiceTest extends React.Component<any, any>{
    public state = {
        testLogin: false
    }
    public paramData = [];
    public constructor(props: any) {
        super(props);
        window['AddpContext']['ServiceTest'] = {};
        this.props.redux.data = {
            "host": "172.17.13.33:15000",
            "interfaceName": "com.nix.jingxun.addp.rpc.producer.test.Hello",
            "appName": "app1",
            "group": "RPC",
            "version": "1.0.0",
            "methods": {
                "methodName": "sayHello",
                "paramType": [
                    "java.lang.String"
                ],
                "returnType": "void"
            }
        }
        this.paramHandler = this.paramHandler.bind(this);
    }
    public setState(state: any) {
        super.setState(state, () => window['AddpContext']['ServiceTest'] = this.state);
    }
    public onSubmit = () => {
        this.setState({
            testLogin: true
        })
        Fetch('/ops/methodTest', {
            method: 'POST',
            data: {
                paramData: this.paramData,
                paramType: this.props.redux.data.methods.paramType,
                interfaceName:this.props.redux.data.interfaceName,
                methodName:this.props.redux.data.methods.methodName,
                appName:this.props.redux.data.appName,
                group:this.props.redux.data.group,
                version:this.props.redux.data.version
            }
        }).then((result: any) => {
            console.log(result);
            this.setState({
                testLogin: false
            })
        }).catch((error) => {
            message.error('請求失敗！！！');
            console.log(error);
            this.setState({
                testLogin: false
            })
        });
    }
    public paramHandler = (index: number, value: any) => {
        console.log('index',index);
        (this.paramData as string[])[index] = value;
        console.log('paramData', this.paramData);

    }
    public render() {
        console.log("serviceTest start...", this.props);
        this.props.dispatch(setNavType('service_test'));
        let num = 0;
        return (
            <div className='conetntDiv'>
                <h3>{`${this.props.redux.data.interfaceName}#${this.props.redux.data.methods.methodName}`}</h3>
                {
                    this.props.redux.data.methods.paramType.map((paramType: string, index?: any) => (
                        <div>
                            <span style={{display:'none'}}>{index = num++}</span>
                            <span>{paramType}</span>:
                            <input className='paramData' type='text' name='paramData' onChange={(e: any) => this.paramHandler(index, e.target.value)} />
                        </div>
                    ))
                }
                <Button type="primary" loading={this.state.testLogin} onClick={this.onSubmit}>
                    测试
                </Button>
            </div>
        );
    }
}
export default withRouter((connect(
    (state: any) => {
        return {
            redux: state.serviceMethod
        }
    },
    (dispatch) => ({
        dispatch: (func: any) => func(dispatch)
    }),
    null,
    { withRef: false }
)(ServiceTest) as ConnectedComponentClass<typeof ServiceTest, any>));