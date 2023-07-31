import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';
import ReactDOM from 'react-dom/client';

import QueryBoundary from './components/@common/QueryBoundary';
import { startWorker } from './mocks/worker';
import Router from './router/Router';
import { ErrorBoundaryValue } from './types/common/errorBoundary';

startWorker();

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      suspense: true,
      retry: false,
      useErrorBoundary: true,
    },
    mutations: {
      useErrorBoundary: true,
    },
  },
});

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

const loadingFallback = <div>loading...</div>;
const errorFallback = ({ reset }: ErrorBoundaryValue) => (
  <button type="button" onClick={reset}>
    retry
  </button>
);

root.render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <QueryBoundary loadingFallback={loadingFallback} errorFallback={errorFallback}>
        <Router />
      </QueryBoundary>
    </QueryClientProvider>
  </React.StrictMode>,
);
