import { combineReducers } from 'redux';
import * as CONST from '../const/index'

export interface IDisDataType<T> {
  type: string,
  data: T,
  sign: string
}
export const nav = (state = {
  type: '',
  data: {
    selectKey: ''
  },
  sign: 'nav'
}, action: IDisDataType<any>) => {
  switch (action.type) {
    case CONST.ADDP_NAV_TYPE: return action;
    default:
      return state;
  }
}
export const index = (state = {
  type: '',
  data: {
    serviceList: []
  },
  sign: 'index'
}, action: IDisDataType<any>) => {
  switch (action.type) {
    case CONST.ADDP_SEARCH_INDEX_DATA: return action;
    default:
      return state;
  }
}
export const detail = (state = {
  type: '',
  data: {
    serviceDetailTable: []
  },
  sign: 'detail'
}, action: IDisDataType<any>) => {
  switch (action.type) {
    case CONST.ADDP_SERVICE_DETAIL: return action;
    default:
      return state;
  }
}
export const serviceMethod = (state = {
  type: '',
  data: {
    method: {
      
    }
  },
  sign: 'detail'
}, action: IDisDataType<any>) => {
  switch (action.type) {
    case CONST.ADDP_SERVICE_METHOD: return action;
    default:
      return state;
  }
}
export default combineReducers({
  nav,
  index,
  detail,
  serviceMethod
})