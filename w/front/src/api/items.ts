import { get, post, put, deleteRequest } from './axiosRequest'

export interface PageBase { pageNumber?: number; pageSize?: number; sortBy?: string; sortOrder?: 'asc'|'desc' }
export interface ItemsSelectionDTO extends PageBase { name?: string; status?: number }
export interface Item {
  id?: number; title: string; type?: string; description?: string; status?: number; placeId?: number;
  contact?: string; imageUrl?: string; createTime?: string; updateTime?: string
}
export interface PageResp<T> { records: T[]; total: number; current: number; size: number }

export const getItemsPages = (payload: ItemsSelectionDTO) => post('/items/pages', payload)
export const createItem = (item: Item) => post('/items', item)
export const updateItem = (item: Item) => put('/items', item)
export const deleteItem = (id: number) => deleteRequest(`/items/${id}`)
export const getItemTypeCount = () => get('/items/itemTypeCount')
