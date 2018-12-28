import * as CONST from '../const'
import Fetch from '../../Fetch';
import { message } from "antd";
import { IDisDataType } from '../reducer';

export const setNavType = (key: string) => (dispatch:(data:IDisDataType<any>)=>void) => {
    dispatch({
        type: CONST.ADDP_NAV_TYPE,
        data: {
            selectKey: key
        },
        sign:'nav'
    });
}
export const serviceSearch = (type:string,key:string) => (dispatch:(data:IDisDataType<any>)=>void) => {
    Fetch(`/ops/search/${type}?key=${key}`, {
        method: 'GET'
    }).then((serviceData: any[]) => {
        serviceData.map(item => item.service = item.interfaceName);
        console.log('data = ', serviceData);
        dispatch({
            type:CONST.ADDP_SEARCH_INDEX_DATA,
            data:{
                serviceList:serviceData
            },
            sign:'index'
        });
    }).catch(error => {
        console.log("error", error);
        message.error('查询服务列表失败！！！');
    });
}
export const serviceDetail = (sign:string) => (dispatch:(data:IDisDataType<any>)=>void) => {
    Fetch(`/ops/producers/?sign=${sign}`, {
        method: 'GET'
    }).then((serviceDetailTable: any[]) => {
        serviceDetailTable = serviceDetailTable.map(item => {
            return {
                ip: item.split(':')[0],
                port: Number.parseInt(item.split(':')[1], 10)
            }
        });
        dispatch({
            type:CONST.ADDP_SERVICE_DETAIL,
            data:{
                serviceDetailTable
            },
            sign:'detail'
        });
    }).catch(error => {
        console.log("error", error);
        message.error('获取服务详情失败！！！');
    });
}