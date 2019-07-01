using System;
using AndroidX.Work;

namespace MyDEFCON.Services
{
    public interface IWorkerService
    {
        void CreateUniquePeriodicWorker<T>(TimeSpan repeatInterval) where T : Worker;
        void CreateUniqueWorker<T>() where T : Worker;
        void CancelWorker<T>() where T : Worker;
        void CancelAllWorker();
    }
    public class WorkerService : IWorkerService
    {
        public void CancelAllWorker()
        {
            WorkManager.Instance.CancelAllWork();
        }

        public void CancelWorker<T>() where T : Worker
        {
            WorkManager.Instance.CancelAllWorkByTag(typeof(T).Name);
        }

        public void CreateUniquePeriodicWorker<T>(TimeSpan repeatInterval) where T : Worker
        {
            PeriodicWorkRequest periodicWorkRequest = PeriodicWorkRequest.Builder.From<T>(repeatInterval).Build();
            WorkManager.Instance.EnqueueUniquePeriodicWork(typeof(T).Name, ExistingPeriodicWorkPolicy.Replace, periodicWorkRequest);
        }

        public void CreateUniqueWorker<T>() where T : Worker
        {
            OneTimeWorkRequest oneTimeWorkRequest = OneTimeWorkRequest.Builder.From<T>().Build();
            WorkManager.Instance.EnqueueUniqueWork(typeof(T).Name, ExistingWorkPolicy.Replace, oneTimeWorkRequest);
        }
    }
}