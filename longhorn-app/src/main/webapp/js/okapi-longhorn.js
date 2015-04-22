/*===========================================================================
  Copyright (C) 2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/
   

   var serviceUrl = window.location.pathname;
   var projectsUrl = serviceUrl + "projects/";

   //load or refresh the project list
   function loadProjectList(){
   		$.get(projectsUrl, function(data) {
			$("#projects").empty();
			$(data).find("e").each(function(){
				$("#projects").append("<tr><td><a class=\"withicon\" OnClick=\"loadProjectDetails("+$(this).text()+");\" href=\"#\">Project: "+$(this).text()+" <img src=\"icons/zoom.png\"></a></td><td><a OnClick=\"deleteProject("+$(this).text()+");\" href=\"#\"><img src=\"icons/cancel.png\"></a></tr> ");
			});
		}
		,"xml");   
	};

	//load project details
	function loadProjectDetails(prjid){

		countInputFiles = 0;
		countOutputFiles = 0;
	
		$("#project").empty().append(prjid);
		$("#toggle_details").show();
		$("#files").hide();
		
   		$.get(projectsUrl+prjid+"/inputFiles", function(data) {
			$("#input_files").empty();
			$(data).find("e").each(function(){
				$("#input_files").append("<tr><td><a href=\""+projectsUrl+prjid+"/inputFiles/"+$(this).text()+"\" target=\"_blank\">"+$(this).text()+"</a></td></tr>");
				countInputFiles=countInputFiles+1;
			});
			if(countInputFiles > 0){
			   $("#files").show();
			}
		}
		,"xml");   
   
   		$.get(projectsUrl+prjid+"/outputFiles", function(data) {
			$("#output_files").empty();
			$(data).find("e").each(function(){
				$("#output_files").append("<tr><td><a href=\""+projectsUrl+prjid+"/outputFiles/"+$(this).text()+"\" target=\"_blank\">"+$(this).text()+"</a></td></tr>");
				countOutputFiles=countOutputFiles+1;
			});
			if(countOutputFiles > 0){
			   $("#files").show();
			}
		}
		,"xml");   
		
	};

	//delete a project
	function deleteProject(id){
   
		$.ajax({
			type: "DELETE",
			url: projectsUrl+id,
			complete: function(msg){
				loadProjectList();
			}
		});
		
		if($("#project").text() == id){
		 $("#toggle_details").hide();
		}
		
    };
	
	function executeProject(){
   
		$.ajax({
			type: "POST",
			url: projectsUrl+$("#project").text()+"/tasks/execute",
			success: function() { 
				alert("Project "+$("#project").text()+" executed");
				loadProjectDetails($("#project").text());
			}, 
			error: function() { 
				alert("Project "+$("#project").text()+" execution failed. Please check batch config and input files.");
			}, 
			dataType: "xml"
		});
   
    };
	
$(document).ready(function() {

   $("#toggle_details").hide();

   $("#list_projects").click(function() {
   	 loadProjectList();
   });
   
   $("#create_project").click(function() {
		$.post(projectsUrl+"new/", function(data) {
			loadProjectList();	 
		}
		,"xml");  
   });
   
   $("#batchForm").ajaxForm({ 
		beforeSubmit:  function(formData, jqForm, options) { 
			options.url=projectsUrl+$("#project").text()+"/batchConfiguration";
		},
		success:    function() { 
			alert("BatchConfiguration File Uploaded!"); 
			loadProjectDetails($("#project").text());
			
		} 
	}); 
	
	$("#inputFileForm").ajaxForm({ 
		beforeSubmit:  function(formData, jqForm, options) {

			//--Handles browser incompatibility in dealing with the path--
		    var fileAndPath = $("#inputFileForm :file").fieldValue()[0];
		    var lastPathDelimiter = fileAndPath.lastIndexOf("\\");
		    var lastPathDelimiterForward = fileAndPath.lastIndexOf("/");
		    var fileNameOnly;
		    
		    if (lastPathDelimiter != -1){
		        fileNameOnly = fileAndPath.substring(lastPathDelimiter+1);
		    }else if (lastPathDelimiterForward != -1){
		    	fileNameOnly = fileAndPath.substring(lastPathDelimiterForward+1);
		    }else{
		    	fileNameOnly = fileAndPath;
		    }
		
			options.url=projectsUrl+$("#project").text()+"/inputFiles/"+fileNameOnly;
		},
		success:    function(responseText, statusText, xhr, $form) { 

			alert("Input File Uploaded!"); 
			loadProjectDetails($("#project").text());
		} 
	}); 
});



