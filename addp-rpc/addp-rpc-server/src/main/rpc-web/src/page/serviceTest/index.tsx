import * as React from 'react';
import { withRouter } from 'react-router-dom';
import {  message } from "antd";
import { connect } from 'react-redux'
import Fetch from '../../Fetch';
class ServiceTest extends React.Component<any, any>{
    public constructor(props: any) {
        super(props);
        window['AddpContext']['ServiceTest'] = {};
    }
    public setState(state: any) {
        super.setState(state, () => window['AddpContext']['ServiceTest'] = this.state);
    }

    // 测试获取服务详情
    public serviceDetail = (sign: string) => {
        Fetch(`/ops/detail/?sign=${sign}`, {
            method: 'GET'
        }).then((detail: any) => {
            console.log('data = ', detail);
        }).catch(error => {
            console.log("error", error);
            message.error('获取服务详情失败！！！');
        });
    }
    
    public render() {
        return (
            <h1>test</h1>
        );
    }
}
export default connect()(withRouter(ServiceTest));