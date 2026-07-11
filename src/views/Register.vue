<template>
  <div class="register-container">
    <div class="register-wrapper">
      <div class="brand-section">
        <div class="brand-logo">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect width="48" height="48" rx="12" fill="url(#grad2)" />
            <path d="M14 24h20M24 14v20" stroke="#fff" stroke-width="3" stroke-linecap="round" />
            <defs>
              <linearGradient id="grad2" x1="0" y1="0" x2="48" y2="48">
                <stop stop-color="#6366f1" />
                <stop offset="1" stop-color="#8b5cf6" />
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="brand-name">BookKeeping</h1>
        <p class="brand-tagline">Create a new account to get started</p>
      </div>

      <el-card class="register-card">
        <h2 class="form-title">Create Account</h2>
        <p class="form-subtitle">Fill in the details below to register</p>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-position="top"
          @keyup.enter="handleRegister"
        >
          <el-form-item label="Username" prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="Choose a username (3-20 characters)"
              clearable
              size="large"
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <el-form-item label="Password" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="Min 6 chars, uppercase & number"
              show-password
              clearable
              size="large"
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <el-form-item label="Confirm Password" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="Re-enter your password"
              show-password
              clearable
              size="large"
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <!-- Password strength indicator -->
          <div class="password-strength">
            <div class="strength-bar">
              <div class="strength-fill" :style="{ width: passwordStrength + '%', background: strengthColor }"></div>
            </div>
            <span class="strength-text" :style="{ color: strengthColor }">
              {{ strengthLabel }}
            </span>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              @click="handleRegister"
              :loading="isLoading"
              class="register-button"
              size="large"
            >
              {{ isLoading ? 'Registering...' : 'Register' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-link">
          <span>Already have an account?</span>
          <router-link to="/login" class="link-text">Sign in here</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const registerFormRef = ref()
const isLoading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const calculatePasswordStrength = () => {
  const password = registerForm.password
  let strength = 0

  if (!password) return 0
  if (password.length >= 8) strength += 25
  if (password.length >= 12) strength += 10
  if (/[A-Z]/.test(password)) strength += 25
  if (/[a-z]/.test(password)) strength += 15
  if (/[0-9]/.test(password)) strength += 25
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength += 10

  return Math.min(strength, 100)
}

const passwordStrength = computed(() => calculatePasswordStrength())

const strengthLabel = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return ''
  if (strength < 30) return 'Weak'
  if (strength < 60) return 'Fair'
  if (strength < 80) return 'Good'
  return 'Strong'
})

const strengthColor = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return '#2a2a3a'
  if (strength < 30) return '#ef4444'
  if (strength < 60) return '#f59e0b'
  if (strength < 80) return '#3b82f6'
  return '#10b981'
})

const validatePasswordMatch = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please confirm your password'))
  } else if (value !== registerForm.password) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

const validatePasswordStrength = (rule, value, callback) => {
  if (!value) {
    callback(new Error('Password is required'))
  } else if (value.length < 6) {
    callback(new Error('Password must be at least 6 characters'))
  } else if (!/[A-Z]/.test(value)) {
    callback(new Error('Password must contain at least one uppercase letter'))
  } else if (!/[0-9]/.test(value)) {
    callback(new Error('Password must contain at least one number'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: 'Username is required', trigger: 'blur' },
    { min: 3, max: 20, message: 'Username must be 3-20 characters', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: 'Username can only contain letters, numbers, and underscores', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' },
    { validator: validatePasswordStrength, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm your password', trigger: 'blur' },
    { validator: validatePasswordMatch, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
  } catch (error) {
    ElMessage.error('Please fix the form errors')
    return
  }

  isLoading.value = true

  try {
    await request.post('/auth/register', {
      username: registerForm.username,
      password: registerForm.password
    })

    ElMessage.success('Registration successful! Redirecting to login...')

    registerForm.username = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''

    setTimeout(() => {
      router.push('/login')
    }, 1000)
  } catch (error) {
    const errorMessage =
      error.response?.data?.message ||
      error.response?.statusText ||
      'Registration failed. Please try again.'

    ElMessage.error(errorMessage)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #0f0f13;
  background-image:
    radial-gradient(ellipse at 80% 50%, rgba(99, 102, 241, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 20% 80%, rgba(139, 92, 246, 0.06) 0%, transparent 50%);
  padding: 20px;
}

.register-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 440px;
}

.brand-section {
  text-align: center;
  margin-bottom: 32px;
}

.brand-logo {
  margin-bottom: 16px;
}

.brand-name {
  font-size: 28px;
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: -0.5px;
}

.brand-tagline {
  font-size: 14px;
  color: #64748b;
  margin-top: 6px;
}

.register-card {
  width: 100%;
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
}

.register-card :deep(.el-card__body) {
  padding: 32px;
}

.form-title {
  font-size: 22px;
  font-weight: 600;
  color: #f1f5f9;
  margin-bottom: 6px;
}

.form-subtitle {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 28px;
}

.register-card :deep(.el-form-item__label) {
  color: #94a3b8;
  font-weight: 500;
  font-size: 13px;
}

.register-card :deep(.el-input__wrapper) {
  background: #12121a;
  border: 1px solid #2a2a3a;
  border-radius: 10px;
  box-shadow: none;
  transition: border-color 0.2s ease;
}

.register-card :deep(.el-input__wrapper:hover) {
  border-color: #6366f1;
}

.register-card :deep(.el-input__wrapper.is-focus) {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}

.register-card :deep(.el-input__inner) {
  color: #f1f5f9;
}

.register-card :deep(.el-input__inner::placeholder) {
  color: #475569;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: #2a2a3a;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.strength-text {
  font-size: 12px;
  font-weight: 600;
  min-width: 44px;
  text-align: right;
}

.register-button {
  width: 100%;
  font-size: 15px;
  font-weight: 600;
  padding: 12px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border: none;
  transition: all 0.3s ease;
  margin-top: 8px;
}

.register-button:hover {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #64748b;
  margin-top: 16px;
}

.link-text {
  margin-left: 6px;
  color: #6366f1;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s ease;
}

.link-text:hover {
  color: #8b5cf6;
}

@media (max-width: 480px) {
  .register-card :deep(.el-card__body) {
    padding: 24px 20px;
  }

  .brand-name {
    font-size: 24px;
  }

  .form-title {
    font-size: 20px;
  }
}
</style>
