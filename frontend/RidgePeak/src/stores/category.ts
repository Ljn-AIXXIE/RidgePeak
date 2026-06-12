import {ref} from "vue";
import api from "../api";
import {defineStore} from "pinia";

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

export const useCategoryStore = defineStore("categoryStore", {
    state: () => {
        return { CategoryState: false }
    },
    actions: {
        setCategoryState(state: boolean) {
            this.$state.CategoryState = state
            CategoryState.value = state
        },
    },
})

export function clearCategoryInfo() {
    CategoryId.value = undefined
    Categories.value = []
}

export async function refreshCategories() {
    const result = await api.getCategories()
    if (!result.success) {
        clearCategoryInfo()
        return {
            success: false,
            message: "获取板块信息失败",
        }
    }

    Categories.value = result.data || []
    return {
        success: true,
        message: "获取板块信息成功",
    }
}