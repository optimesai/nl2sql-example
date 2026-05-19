<script setup lang="ts">
import { ref } from 'vue';

interface Message {
  role: 'user' | 'assistant';
  content: string;
  sql?: string;
  data?: any[];
}

const question = ref('');
const messages = ref<Message[]>([]);
const isLoading = ref(false);

const ask = async () => {
  if (!question.value.trim() || isLoading.value) return;

  const userMsg = question.value;
  messages.value.push({ role: 'user', content: userMsg });
  question.value = '';
  isLoading.value = true;

  try {
    const response = await fetch('/api/query', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ question: userMsg })
    });
    
    const result = await response.json();
    messages.value.push({
      role: 'assistant',
      content: result.answer,
      sql: result.generatedSql,
      data: result.data
    });
  } catch (error) {
    messages.value.push({ role: 'assistant', content: '오류가 발생했습니다. 서버 연결을 확인해주세요.' });
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="flex h-screen bg-gray-900 text-white font-sans">
    <!-- Sidebar: Dashboard/Stats -->
    <aside class="w-80 border-r border-gray-800 p-6 flex flex-col gap-6">
      <h1 class="text-xl font-bold bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">
        Production NL2SQL
      </h1>
      
      <div class="flex flex-col gap-4">
        <h2 class="text-sm font-semibold text-gray-400 uppercase tracking-wider">Dashboard</h2>
        <div class="grid grid-cols-1 gap-3">
          <div class="bg-gray-800 p-4 rounded-xl border border-gray-700">
            <div class="text-xs text-gray-400">Total Factory Lines</div>
            <div class="text-2xl font-bold mt-1">4</div>
          </div>
          <div class="bg-gray-800 p-4 rounded-xl border border-gray-700">
            <div class="text-xs text-gray-400">Products Tracked</div>
            <div class="text-2xl font-bold mt-1">12</div>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main Content: Chat -->
    <main class="flex-1 flex flex-col relative overflow-hidden">
      <!-- Chat Messages -->
      <div class="flex-1 overflow-y-auto p-6 space-y-6 pb-32">
        <div v-if="messages.length === 0" class="h-full flex flex-col items-center justify-center text-gray-500 gap-4">
          <div class="w-16 h-16 rounded-full bg-gray-800 flex items-center justify-center text-2xl">🤖</div>
          <p>생산 관리 데이터에 대해 무엇이든 물어보세요.</p>
        </div>

        <div v-for="(msg, i) in messages" :key="i" 
             :class="['flex flex-col gap-2', msg.role === 'user' ? 'items-end' : 'items-start']">
          <div :class="['max-w-[80%] p-4 rounded-2xl shadow-lg', 
                       msg.role === 'user' ? 'bg-blue-600' : 'bg-gray-800 border border-gray-700']">
            <p class="whitespace-pre-wrap">{{ msg.content }}</p>
          </div>
          
          <!-- SQL & Data Preview for Assistant -->
          <div v-if="msg.sql" class="w-full max-w-[90%] space-y-3 mt-2">
            <details class="bg-black/30 rounded-lg overflow-hidden border border-gray-700">
              <summary class="px-3 py-2 text-xs font-mono text-emerald-400 cursor-pointer hover:bg-white/5">
                Generated SQL
              </summary>
              <pre class="p-3 text-xs font-mono text-gray-300 overflow-x-auto">{{ msg.sql }}</pre>
            </details>
            
            <div v-if="msg.data && msg.data.length > 0" class="overflow-x-auto rounded-lg border border-gray-700 bg-gray-800/50">
              <table class="w-full text-xs text-left">
                <thead class="bg-gray-700 text-gray-300">
                  <tr>
                    <th v-for="key in Object.keys(msg.data[0])" :key="key" class="px-3 py-2 font-semibold">
                      {{ key }}
                    </th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-700">
                  <tr v-for="(row, ri) in msg.data" :key="ri" class="hover:bg-white/5">
                    <td v-for="val in Object.values(row)" :key="String(val)" class="px-3 py-2 text-gray-300">
                      {{ val }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="absolute bottom-0 left-0 right-0 p-6 bg-gradient-to-t from-gray-900 via-gray-900 to-transparent">
        <div class="max-w-4xl mx-auto relative">
          <input 
            v-model="question" 
            @keyup.enter="ask"
            type="text" 
            placeholder="예: 이번 달 생산량이 가장 많은 라인은 어디야?"
            class="w-full bg-gray-800 border border-gray-700 rounded-2xl px-6 py-4 pr-16 focus:outline-none focus:ring-2 focus:ring-blue-500 shadow-2xl placeholder-gray-500"
            :disabled="isLoading"
          />
          <button 
            @click="ask"
            class="absolute right-3 top-1/2 -translate-y-1/2 p-2 bg-blue-600 hover:bg-blue-500 rounded-xl transition-colors disabled:opacity-50"
            :disabled="isLoading"
          >
            <span v-if="!isLoading">🚀</span>
            <span v-else class="animate-spin inline-block">⌛</span>
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<style>
/* Custom scrollbar for dark theme */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: #374151;
  border-radius: 10px;
}
::-webkit-scrollbar-thumb:hover {
  background: #4b5563;
}
</style>
