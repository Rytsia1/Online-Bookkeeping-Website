<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="brand-section">
        <div class="brand-logo">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <rect width="48" height="48" rx="12" fill="url(#grad)" />
            <path d="M14 24h20M24 14v20" stroke="#fff" stroke-width="3" stroke-linecap="round" />
            <defs>
              <linearGradient id="grad" x1="0" y1="0" x2="48" y2="48">
                <stop stop-color="#6366f1" />
                <stop offset="1" stop-color="#8b5cf6" />
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="brand-name">BookKeeping</h1>
        <p class="brand-tagline">Manage your finances with ease</p>
      </div>

      <el-card class="login-card">
        <h2 class="form-title">Sign In</h2>
        <p class="form-subtitle">Enter your credentials to continue</p>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          label-position="top"
          @keyup.enter="handleLogin"
        >
          <el-form-item label="Username" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="Enter your username"
              clearable
              size="large"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item label="Password" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="Enter your password"
              show-password
              clearable
              size="large"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              @click="handleLogin"
              :loading="isLoading"
              class="login-button"
              size="large"
            >
              {{ isLoading ? 'Signing in...' : 'Sign In' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-link">
          <span>Don't have an account?</span>
          <router-link to="/register" class="link-text">Register now</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loginFormRef = ref()
const isLoading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

// Validation rules for login form
const loginRules = {
  username: [
    {
      required: true,
      message: 'Username is required',
      trigger: 'blur'
    },
    {
      min: 3,
      max: 20,
      message: 'Username must be between 3 and 20 characters',
      trigger: 'blur'
    }
  ],
  password: [
    {
      required: true,
      message: 'Password is required',
      trigger: 'blur'
    },
    {
      min: 6,
      message: 'Password must be at least 6 characters',
      trigger: 'blur'
    }
  ]
}

/**
 * Handle login action
 * Validate form before making API call
 */
const handleLogin = async () => {
  // Validate form first
  try {
    await loginFormRef.value.validate()
  } catch (error) {
    ElMessage.error('Please fix the form errors')
    return
  }

  isLoading.value = true

  try {
    // Make API call to login endpoint
    const response = await request.post('/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })

    // Save token to localStorage
    // response interceptor already unwraps response.data
    const token = response.token || response.data?.token
    if (token) {
      localStorage.setItem('token', token)
      localStorage.setItem('username', loginForm.username)
      ElMessage.success('Login successful!')

      // Redirect to dashboard after a short delay
      setTimeout(() => {
        router.push('/dashboard')
      }, 500)
    } else {
      ElMessage.error('Login failed: No token received')
    }
  } catch (error) {
    // Handle error response
    const errorMessage =
      error.response?.data?.message ||
      error.response?.statusText ||
      'Login failed. Please try again.'

    ElMessage.error(errorMessage)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #0f0f13;
  background-image:
    radial-gradient(ellipse at 20% 50%, rgba(99, 102, 241, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(139, 92, 246, 0.06) 0%, transparent 50%);
  padding: 20px;
}

.login-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 420px;
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

.login-card {
  width: 100%;
  background: #1a1a24;
  border: 1px solid #2a2a3a;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
}

.login-card :deep(.el-card__body) {
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

.login-card :deep(.el-form-item__label) {
  color: #94a3b8;
  font-weight: 500;
  font-size: 13px;
}

.login-card :deep(.el-input__wrapper) {
  background: #12121a;
  border: 1px solid #2a2a3a;
  border-radius: 10px;
  box-shadow: none;
  transition: border-color 0.2s ease;
}

.login-card :deep(.el-input__wrapper:hover) {
  border-color: #6366f1;
}

.login-card :deep(.el-input__wrapper.is-focus) {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}

.login-card :deep(.el-input__inner) {
  color: #f1f5f9;
}

.login-card :deep(.el-input__inner::placeholder) {
  color: #475569;
}

.login-button {
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

.login-button:hover {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);
}

.register-link {
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
  .login-card :deep(.el-card__body) {
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
