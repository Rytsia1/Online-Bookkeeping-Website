<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEditMode ? 'Edit Tagihan' : 'Tambah Tagihan Baru'"
    width="500px"
    @close="handleDialogClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      label-position="top"
    >
      <el-form-item label="Tipe" prop="type">
        <el-select v-model="formData.type" placeholder="Pilih tipe">
          <el-option label="Pemasukan" value="income" />
          <el-option label="Pengeluaran" value="expense" />
        </el-select>
      </el-form-item>

      <el-form-item label="Kategori" prop="category">
        <el-select v-model="formData.category" placeholder="Pilih kategori">
          <el-option
            v-for="cat in categoryOptions"
            :key="cat"
            :label="cat"
            :value="cat"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="Jumlah (Rp)" prop="amount">
        <el-input
          v-model.number="formData.amount"
          type="number"
          placeholder="0"
          min="0"
        />
      </el-form-item>

      <el-form-item label="Tanggal" prop="date">
        <el-date-picker
          v-model="formData.date"
          type="date"
          placeholder="Pilih tanggal"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>

      <el-form-item label="Deskripsi" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          placeholder="Masukkan deskripsi (opsional)"
          rows="3"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleDialogClose">Batal</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          {{ isEditMode ? 'Simpan Perubahan' : 'Tambah Tagihan' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  editData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const formRef = ref(null)
const loading = ref(false)

const isEditMode = computed(() => !!props.editData)

const categoryOptions = computed(() => {
  if (formData.type === 'income') {
    return ['Gaji', 'Bonus', 'Freelance', 'Investasi', 'Lainnya']
  }
  return ['Makanan', 'Transport', 'Utilitas', 'Belanja', 'Hiburan', 'Lainnya']
})

const formData = ref({
  type: 'expense',
  category: '',
  amount: '',
  date: new Date().toISOString().split('T')[0],
  description: ''
})

const formRules = {
  type: [
    { required: true, message: 'Tipe harus dipilih', trigger: 'change' }
  ],
  category: [
    { required: true, message: 'Kategori harus dipilih', trigger: 'change' }
  ],
  amount: [
    { required: true, message: 'Jumlah harus diisi', trigger: 'blur' },
    {
      type: 'number',
      min: 1,
      message: 'Jumlah harus lebih dari 0',
      trigger: 'blur'
    }
  ],
  date: [
    { required: true, message: 'Tanggal harus dipilih', trigger: 'change' }
  ]
}

// Watch untuk reset form ketika mode berubah
watch(
  () => props.editData,
  (newVal) => {
    if (newVal) {
      formData.value = { ...newVal }
    } else {
      resetForm()
    }
  }
)

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    loading.value = true

    const payload = {
      type: formData.value.type,
      category: formData.value.category,
      amount: formData.value.amount,
      date: formData.value.date,
      description: formData.value.description || ''
    }

    if (isEditMode.value) {
      // Update existing bill
      await request.put(`/bills/${formData.value.id}`, payload)
      ElMessage.success('Tagihan berhasil diupdate')
    } else {
      // Create new bill
      await request.post('/bills', payload)
      ElMessage.success('Tagihan berhasil ditambahkan')
    }

    emit('success')
    handleDialogClose()
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('Gagal menyimpan tagihan')
    }
  } finally {
    loading.value = false
  }
}

const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

const resetForm = () => {
  formData.value = {
    type: 'expense',
    category: '',
    amount: '',
    date: new Date().toISOString().split('T')[0],
    description: ''
  }
  formRef.value?.clearValidate()
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
