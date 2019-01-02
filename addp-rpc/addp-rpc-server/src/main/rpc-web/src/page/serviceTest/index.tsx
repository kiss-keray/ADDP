import * as React from 'react';
import { withRouter } from 'react-router-dom';
import { connect,ConnectedComponentClass } from 'react-redux'
import { setNavType } from '../../rpc-redux/actions'
class ServiceTest extends React.Component<any, any>{
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
    }
    public setState(state: any) {
        super.setState(state, () => window['AddpContext']['ServiceTest'] = this.state);
    }
    public render() {
        console.log("serviceTest start...",this.props);
        this.props.dispatch(setNavType('service_test'));
        return (
            <h1>test</h1>
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