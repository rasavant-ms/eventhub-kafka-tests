using Microsoft.Azure.EventHubs;
using Microsoft.Azure.WebJobs;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;


namespace ReadEventHubs
{
    public static class SendToCosmosDB
    {
        [FunctionName("SendToCosmosDB")]
        public static async Task Run(
            [EventHubTrigger(
                "%EventHubName%", 
                Connection = "EventHubConnectionAppSetting", 
                ConsumerGroup = "%ConsumerGroupCosmos%")
            ] EventData[] eventHubData,
            [CosmosDB(
                databaseName: "%databaseName%",
                collectionName: "%collectionName%",
                ConnectionStringSetting = "CosmosDBConnection")]
                IAsyncCollector<object> toDoItemsOut,
            ILogger log)
        {
            //log.LogInformation($"C# Event Hub trigger function processed a message: {eventHubData.Length.ToString()}");

            log.LogInformation("Event Hub batch size - " + eventHubData.Length.ToString());

            List<Task> tasks = new List<Task>();
            log.LogDebug($"This is debug message");
            log.LogTrace("This is trace message");
            log.LogWarning("This is warning message");
            log.LogError("This is error message");
            foreach (EventData message in eventHubData)
            {
                //log.LogInformation($"Description={Encoding.UTF8.GetString(message.Body.Array)}");

                object outData = new
                {
                    eventData = JObject.Parse(Encoding.UTF8.GetString(message.Body.Array)),
                    enqueuedAt = message.SystemProperties.EnqueuedTimeUtc,
                    storedAt = DateTime.UtcNow
                };
                

                tasks.Add(toDoItemsOut.AddAsync(outData));
            }

            await Task.WhenAll(tasks);
        }
    }
}
