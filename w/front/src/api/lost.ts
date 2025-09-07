import { get, post, put, deleteRequest } from './axiosRequest'

export interface LostSelectionDTO { pageNumber?: number; pageSize?: number; name?: string; status?: number }
export interface LostNotice { id?: number; title: string; description?: string; placeId?: number; contact?: string; status?: number; createTime?: string; updateTime?: string }

export const getLostPages = (payload: LostSelectionDTO) => post('/lost/pages', payload)
export const getLostById = (id: number) => get(`/lost/${id}`)
export const createLost = (data: LostNotice) => post('/lost/', data)
export const updateLost = (data: LostNotice) => put('/lost/', data)
export const deleteLost = (id: number) => deleteRequest(`/lost/${id}`)
