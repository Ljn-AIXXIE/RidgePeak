import {ref} from "vue";
import api from "../api";

export interface Category {
    id: number;
    name: string;
    description?: string;
}

export const CategoryState = ref(false)

export const Categories = ref<Array<Category>>([])
export const CategoryId = ref<number | undefined>()

export const Default = {
    description: '无题解',
}

export function clearCategoryInfo() {
    CategoryState.value = false
    CategoryId.value = undefined
    Categories.value = []
}

export async function refreshCategories() {
    const result = await api.getCategories()
    if (!result.success) {
        clearCategoryInfo()
        return
    }

    Categories.value = result.data || []
    CategoryState.value = true
}